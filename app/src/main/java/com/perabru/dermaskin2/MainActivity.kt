package com.perabru.dermaskin2

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var txtMode: TextView
    private lateinit var containerName: LinearLayout
    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var imgEye: ImageView
    private lateinit var checkTerms: CheckBox
    private lateinit var btnRegister: TextView
    private lateinit var btnLogin: TextView
    private lateinit var txtForgotPassword: TextView
    private lateinit var progressBar: ProgressBar

    private var isLoginMode = false
    private var senhaVisivel = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val txtAppName = findViewById<TextView>(R.id.txtAppName)
        val nomeApp = SpannableString("DermaPrev")

        nomeApp.setSpan(
            ForegroundColorSpan(Color.parseColor("#063B78")),
            0,
            5,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        nomeApp.setSpan(
            ForegroundColorSpan(Color.parseColor("#08AFC0")),
            5,
            9,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        txtAppName.text = nomeApp

        auth = FirebaseAuth.getInstance()

        iniciarComponentes()
        configurarEstadoInicial()
        configurarCliques()

        val usuarioAtual = auth.currentUser

        if (usuarioAtual != null) {
            openAnaliseScreen()
        }
    }

    private fun iniciarComponentes() {

        txtMode = findViewById(R.id.txtMode)
        containerName = findViewById(R.id.containerName)
        edtName = findViewById(R.id.edtName)
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        imgEye = findViewById(R.id.imgEye)
        checkTerms = findViewById(R.id.checkTerms)
        btnRegister = findViewById(R.id.btnRegister)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun configurarEstadoInicial() {

        progressBar.visibility = View.GONE

        changeToRegisterMode()
    }

    private fun configurarCliques() {

        btnRegister.setOnClickListener {

            if (isLoginMode) {
                changeToRegisterMode()
            } else {
                registerUser()
            }
        }

        btnLogin.setOnClickListener {

            if (isLoginMode) {
                loginUser()
            } else {
                changeToLoginMode()
            }
        }

        // MOSTRAR / OCULTAR SENHA
        imgEye.setOnClickListener {
            alternarVisibilidadeSenha()
        }

        // RECUPERAR / ALTERAR SENHA
        txtForgotPassword.setOnClickListener {
            recuperarSenha()
        }
    }

    // =========================================================
    // OLHO DA SENHA
    // =========================================================

    private fun alternarVisibilidadeSenha() {

        senhaVisivel = !senhaVisivel

        if (senhaVisivel) {

            // Senha VISÍVEL
            edtPassword.transformationMethod =
                HideReturnsTransformationMethod.getInstance()

            // Olho ABERTO
            imgEye.setImageResource(
                R.drawable.ic_eye_dermaprev
            )

        } else {

            // Senha OCULTA
            edtPassword.transformationMethod =
                PasswordTransformationMethod.getInstance()

            // Olho FECHADO
            imgEye.setImageResource(
                R.drawable.ic_eye_off_dermaprev
            )
        }

        edtPassword.setSelection(
            edtPassword.text.length
        )
    }


    private fun ocultarSenha() {

        senhaVisivel = false

        edtPassword.transformationMethod =
            PasswordTransformationMethod.getInstance()

        // Senha começa oculta → olho fechado
        imgEye.setImageResource(
            R.drawable.ic_eye_off_dermaprev
        )

        edtPassword.setSelection(
            edtPassword.text.length
        )
    }

    // =========================================================
    // MODO LOGIN
    // =========================================================

    private fun changeToLoginMode() {

        isLoginMode = true

        limparErros()

        txtMode.text = "Entrar na conta"

        containerName.visibility = View.GONE

        // MOSTRA RECUPERAR SENHA SOMENTE NO LOGIN
        txtForgotPassword.visibility = View.VISIBLE

        checkTerms.visibility = View.GONE

        btnLogin.text = "Entrar"
        btnRegister.text = "Criar nova conta"

        edtName.text.clear()
        edtEmail.text.clear()
        edtPassword.text.clear()

        checkTerms.isChecked = false

        ocultarSenha()
    }

    // =========================================================
    // MODO CRIAR CONTA
    // =========================================================

    private fun changeToRegisterMode() {

        isLoginMode = false

        limparErros()

        txtMode.text = "Criar conta"

        containerName.visibility = View.VISIBLE

        // ESCONDE RECUPERAR SENHA NO CADASTRO
        txtForgotPassword.visibility = View.GONE

        checkTerms.visibility = View.GONE

        btnRegister.text = "Criar conta"
        btnLogin.text = "Já tenho conta"

        edtName.text.clear()
        edtEmail.text.clear()
        edtPassword.text.clear()

        checkTerms.isChecked = false

        ocultarSenha()
    }

    // =========================================================
    // CRIAR CONTA
    // =========================================================

    private fun registerUser() {

        val name = edtName.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val password = edtPassword.text.toString().trim()

        if (!validarCadastro(name, email, password)) {
            return
        }

        setLoading(true)

        auth.createUserWithEmailAndPassword(email, password)

            .addOnSuccessListener { result ->

                val userId = result.user?.uid

                if (userId.isNullOrEmpty()) {

                    setLoading(false)

                    Toast.makeText(
                        this,
                        "Erro ao obter ID do usuário.",
                        Toast.LENGTH_LONG
                    ).show()

                    return@addOnSuccessListener
                }

                saveUserData(
                    userId,
                    name,
                    email
                )
            }

            .addOnFailureListener { error ->

                setLoading(false)

                val message = when (error) {

                    is FirebaseAuthUserCollisionException ->
                        "Este e-mail já está cadastrado. Tente entrar na conta."

                    is FirebaseAuthInvalidCredentialsException ->
                        "E-mail inválido. Verifique e tente novamente."

                    else ->
                        "Erro ao criar conta: ${error.message}"
                }

                Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun validarCadastro(
        name: String,
        email: String,
        password: String
    ): Boolean {

        limparErros()

        when {

            name.isEmpty() -> {

                edtName.error = "Informe seu nome"
                edtName.requestFocus()

                return false
            }

            email.isEmpty() -> {

                edtEmail.error = "Informe seu e-mail"
                edtEmail.requestFocus()

                return false
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {

                edtEmail.error = "Informe um e-mail válido"
                edtEmail.requestFocus()

                return false
            }

            password.isEmpty() -> {

                edtPassword.error = "Informe sua senha"
                edtPassword.requestFocus()

                return false
            }

            password.length < 6 -> {

                edtPassword.error =
                    "A senha precisa ter pelo menos 6 caracteres"

                edtPassword.requestFocus()

                return false
            }
        }

        return true
    }

    // =========================================================
    // SALVAR USUÁRIO
    // =========================================================

    private fun saveUserData(
        userId: String,
        name: String,
        email: String
    ) {

        val database =
            FirebaseDatabase.getInstance().reference

        val userData = hashMapOf(
            "id" to userId,
            "nome" to name,
            "email" to email,
            "dataCadastro" to System.currentTimeMillis()
        )

        database.child("usuarios")
            .child(userId)
            .setValue(userData)

            .addOnSuccessListener {

                setLoading(false)

                Toast.makeText(
                    this,
                    "Conta criada com sucesso!",
                    Toast.LENGTH_LONG
                ).show()

                getSharedPreferences(
                    "app_preferences",
                    MODE_PRIVATE
                )
                    .edit()
                    .putBoolean(
                        "lgpd_accepted",
                        false
                    )
                    .putBoolean(
                        "health_profile_completed",
                        false
                    )
                    .apply()

                openAnaliseScreen()
            }

            .addOnFailureListener { error ->

                setLoading(false)

                Toast.makeText(
                    this,
                    "Conta criada, mas houve erro ao salvar os dados: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()

                openAnaliseScreen()
            }
    }

    // =========================================================
    // LOGIN
    // =========================================================

    private fun loginUser() {

        val email =
            edtEmail.text.toString().trim()

        val password =
            edtPassword.text.toString().trim()

        if (!validarLogin(email, password)) {
            return
        }

        setLoading(true)

        auth.signInWithEmailAndPassword(
            email,
            password
        )

            .addOnSuccessListener {

                setLoading(false)

                Toast.makeText(
                    this,
                    "Login realizado com sucesso!",
                    Toast.LENGTH_SHORT
                ).show()

                openAnaliseScreen()
            }

            .addOnFailureListener {

                setLoading(false)

                Toast.makeText(
                    this,
                    "E-mail ou senha incorretos.",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun validarLogin(
        email: String,
        password: String
    ): Boolean {

        limparErros()

        when {

            email.isEmpty() -> {

                edtEmail.error =
                    "Informe seu e-mail"

                edtEmail.requestFocus()

                return false
            }

            !Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches() -> {

                edtEmail.error =
                    "Informe um e-mail válido"

                edtEmail.requestFocus()

                return false
            }

            password.isEmpty() -> {

                edtPassword.error =
                    "Informe sua senha"

                edtPassword.requestFocus()

                return false
            }
        }

        return true
    }

    // =========================================================
    // RECUPERAR / ALTERAR SENHA
    // =========================================================

    private fun recuperarSenha() {

        val email =
            edtEmail.text.toString().trim()

        if (email.isEmpty()) {

            edtEmail.error =
                "Informe seu e-mail"

            edtEmail.requestFocus()

            Toast.makeText(
                this,
                "Digite seu e-mail acima para recuperar ou alterar sua senha.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        if (
            !Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()
        ) {

            edtEmail.error =
                "Informe um e-mail válido"

            edtEmail.requestFocus()

            return
        }

        setLoading(true)

        auth.useAppLanguage()

        auth.sendPasswordResetEmail(email)

            .addOnCompleteListener { task ->

                setLoading(false)

                if (task.isSuccessful) {

                    Toast.makeText(
                        this,
                        "Enviamos um link para o e-mail informado. Abra a mensagem para criar uma nova senha.",
                        Toast.LENGTH_LONG
                    ).show()

                } else {

                    Toast.makeText(
                        this,
                        "Não foi possível enviar o e-mail. Verifique o endereço informado e tente novamente.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    // =========================================================
    // ABRIR PRÓXIMA TELA
    // =========================================================

    private fun openAnaliseScreen() {

        val sharedPreferences =
            getSharedPreferences(
                "app_preferences",
                MODE_PRIVATE
            )

        val lgpdAccepted =
            sharedPreferences.getBoolean(
                "lgpd_accepted",
                false
            )

        val healthProfileCompleted =
            sharedPreferences.getBoolean(
                "health_profile_completed",
                false
            )

        when {

            !lgpdAccepted -> {

                val intent =
                    Intent(
                        this,
                        ConsentActivity::class.java
                    )

                startActivity(intent)
            }

            !healthProfileCompleted -> {

                val intent =
                    Intent(
                        this,
                        HealthProfileActivity::class.java
                    )

                startActivity(intent)
            }

            else -> {

                val intent =
                    Intent(
                        this,
                        AnaliseActivity::class.java
                    )

                startActivity(intent)
            }
        }

        finish()
    }

    // =========================================================
    // LOADING
    // =========================================================

    private fun setLoading(
        isLoading: Boolean
    ) {

        progressBar.visibility =
            if (isLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }

        btnRegister.isEnabled = !isLoading
        btnLogin.isEnabled = !isLoading
        txtForgotPassword.isEnabled = !isLoading

        edtName.isEnabled = !isLoading
        edtEmail.isEnabled = !isLoading
        edtPassword.isEnabled = !isLoading

        imgEye.isEnabled = !isLoading
        checkTerms.isEnabled = !isLoading

        btnRegister.alpha =
            if (isLoading) {
                0.55f
            } else {
                1.0f
            }

        btnLogin.alpha =
            if (isLoading) {
                0.55f
            } else {
                1.0f
            }

        txtForgotPassword.alpha =
            if (isLoading) {
                0.55f
            } else {
                1.0f
            }
    }

    private fun limparErros() {

        edtName.error = null
        edtEmail.error = null
        edtPassword.error = null
    }
}