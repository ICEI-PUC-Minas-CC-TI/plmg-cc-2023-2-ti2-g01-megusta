function testeLogin() {
  const user = sessionStorage.getItem('username');

  if (user) {
    setTimeout(() => {
      window.location.href = 'postarReceita.html';
    }, 1000);
  } else {
    setTimeout(() => {
      window.location.href = 'signin.html';
    }, 1000);
  }
}

function testeLogin1() {
  const user = sessionStorage.getItem('username');
			
  if (user) {
    const buscarUserURL = 'http://localhost:6789/obterUsuario1';
    
    fetch(buscarUserURL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded', 
      },
      body: `user=${user}`, // Corrected parameter name
    })
    .then(response => response.json())
    .then(data => {
      console.log('Data:', data); 
      setTimeout(() => {
        window.location.href = `perfil.html?id=${data.object.id}`;
      }, 1000);
    })
  } else {
    setTimeout(() => {
      window.location.href = 'signin.html';
    }, 1000);
  }
}

function testeLogin2() {
  const user = sessionStorage.getItem('username');
			
  if (user) {
    const buscarUserURL = 'http://localhost:6789/obterUsuario1';
    
    fetch(buscarUserURL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded', 
      },
      body: `user=${user}`, 
    })
    .then(response => response.json())
    .then(data => {
      console.log('Data:', data); 
      setTimeout(() => {
        window.location.href = `feed.html`;
      }, 1000);
    })
  } else {
    setTimeout(() => {
      window.location.href = 'signin.html';
    }, 1000);
  }
}
