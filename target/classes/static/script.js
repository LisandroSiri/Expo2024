function enviarPregunta() {
    const pregunta = document.getElementById('pregunta').value;
    const respuestaDiv = document.getElementById('respuesta');

    if (pregunta.trim() === '') {
        respuestaDiv.innerHTML = 'Por favor, escribe una pregunta.';
        return;
    }

    respuestaDiv.innerHTML = 'Buscando respuesta...';

    fetch(`/pregunta?pregunta=${encodeURIComponent(pregunta)}`)
        .then(response => response.text())
        .then(data => {
            respuestaDiv.innerHTML = data;
        })
        .catch(error => {
            respuestaDiv.innerHTML = 'Error al obtener la respuesta. Por favor, intenta de nuevo.';
            console.error('Error:', error);
        });
}