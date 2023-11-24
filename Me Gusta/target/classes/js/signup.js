



let nome = document.querySelector('#nome')
let labelNome = document.querySelector('#labelNome')
let validNome = false

let sobrenome = document.querySelector('#sobrenome')
let labelSobrenome = document.querySelector('#labelSobrenome')
let validSobrenome = false

let usuario = document.querySelector('#usuario')
let labelUsuario = document.querySelector('#labelUsuario')
let validUsuario = false

let email = document.querySelector('#email')
let labelEmail = document.querySelector('#labelEmail')
let validEmail = false

let senha = document.querySelector('#senha')
let labelSenha = document.querySelector('#labelSenha')
let validSenha = false

let confirmSenha = document.querySelector('#confirmSenha')
let labelConfirmSenha = document.querySelector('#labelConfirmSenha')
let validConfirmSenha = false

let msgError = document.querySelector('#msgError')
let msgSuccess = document.querySelector('#msgSuccess')

nome.addEventListener('keyup', () => {
  if(nome.value.length <= 2){
    labelNome.setAttribute('style', 'color: red')
    labelNome.innerHTML = 'Nome *Insira no minimo 3 caracteres'
    nome.setAttribute('style', 'border-color: red')
    validNome = false
  } else if(nome.value.length > 50){
    labelNome.setAttribute('style', 'color: red')
    labelNome.innerHTML = 'Nome *Limite de 50 caracteres atingido'
    nome.setAttribute('style', 'border-color: red')
    validNome = false
  } else {
    labelNome.setAttribute('style', 'color: #FA7D00')
    labelNome.innerHTML = 'Nome'
    nome.setAttribute('style', 'border-color: #FA7D00')
    validNome = true
  }
})

sobrenome.addEventListener('keyup', () => {
    if(sobrenome.value.length <= 2){
      labelSobrenome.setAttribute('style', 'color: red')
      labelSobrenome.innerHTML = 'Sobrenome *Insira no minimo 3 caracteres'
      sobrenome.setAttribute('style', 'border-color: red')
      validSobrenome = false
    } else if(sobrenome.value.length > 60){
      labelSobrenome.setAttribute('style', 'color: red')
      labelSobrenome.innerHTML = 'Sobrenome *Limite de 60 caracteres atingido'
      sobrenome.setAttribute('style', 'border-color: red')
      validSobrenome = false
    } else {
      labelSobrenome.setAttribute('style', 'color: #FA7D00')
      labelSobrenome.innerHTML = 'Sobrenome'
      sobrenome.setAttribute('style', 'border-color: #FA7D00')
      validSobrenome = true
    }
  })

usuario.addEventListener('keyup', () => {
  if(usuario.value.length <= 4){
    labelUsuario.setAttribute('style', 'color: red')
    labelUsuario.innerHTML = 'Usuário *Insira no minimo 5 caracteres'
    usuario.setAttribute('style', 'border-color: red')
    validUsuario = false
  } else if(usuario.value.length > 20){
    labelUsuario.setAttribute('style', 'color: red')
    labelUsuario.innerHTML = 'Usuario *Limite de 20 caracteres atingido'
    usuario.setAttribute('style', 'border-color: red')
    validUsuario = false
  } else {
    labelUsuario.setAttribute('style', 'color: #FA7D00')
    labelUsuario.innerHTML = 'Usuário'
    usuario.setAttribute('style', 'border-color: #FA7D00')
    validUsuario = true
  }
})

email.addEventListener('keyup', () => {
  if (email.value.length <= 9) {
    labelEmail.setAttribute('style', 'color: red')
    labelEmail.innerHTML = 'E-mail *Insira no mínimo 10 caracteres'
    email.setAttribute('style', 'border-color: red')
    validEmail = false
  } else if (usuario.value.length > 100) {
	labelEmail.setAttribute('style', 'color: red')
    labelEmail.innerHTML = 'Usuario *Limite de 20 caracteres atingido'
    email.setAttribute('style', 'border-color: red')
    validEmail = false
  } else if (!email.value.includes('@')) {
    labelEmail.setAttribute('style', 'color: red')
    labelEmail.innerHTML = 'E-mail *Insira um e-mail válido'
    email.setAttribute('style', 'border-color: red')
    validEmail = false
  } else {
    labelEmail.setAttribute('style', 'color: #FA7D00')
    labelEmail.innerHTML = 'E-mail'
    email.setAttribute('style', 'border-color: #FA7D00')
    validEmail = true
  }
});

senha.addEventListener('keyup', () => {
  if(senha.value.length <= 5){
    labelSenha.setAttribute('style', 'color: red')
    labelSenha.innerHTML = 'Senha *Insira no minimo 6 caracteres'
    senha.setAttribute('style', 'border-color: red')
    validSenha = false
  } else {
    labelSenha.setAttribute('style', 'color: #FA7D00')
    labelSenha.innerHTML = 'Senha'
    senha.setAttribute('style', 'border-color: #FA7D00')
    validSenha = true
  }
})

confirmSenha.addEventListener('keyup', () => {
  if(senha.value != confirmSenha.value){
    labelConfirmSenha.setAttribute('style', 'color: red')
    labelConfirmSenha.innerHTML = 'Confirmar Senha *As senhas não conferem'
    confirmSenha.setAttribute('style', 'border-color: red')
    validConfirmSenha = false
  } else {
    labelConfirmSenha.setAttribute('style', 'color: #FA7D00')
    labelConfirmSenha.innerHTML = 'Confirmar Senha'
    confirmSenha.setAttribute('style', 'border-color: #FA7D00')
    validConfirmSenha = true
  }
})
