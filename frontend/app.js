const apiUrl = 'http://localhost:8080/api/livros';
const statusEl = document.getElementById('status');
const form = document.getElementById('livroForm');
const livroIdInput = document.getElementById('livroId');
const submitBtn = document.getElementById('submitBtn');
const cancelBtn = document.getElementById('cancelBtn');
const tbody = document.getElementById('livroTableBody');

function setStatus(message, type) {
    statusEl.textContent = message;
    statusEl.className = 'status ' + type;
}

function getLivroPayload() {
    const exemplaresValue = document.getElementById('exemplares').value.trim();
    return {
        titulo: document.getElementById('titulo').value.trim(),
        autor: document.getElementById('autor').value.trim(),
        isbn: document.getElementById('isbn').value.trim(),
        exemplares: exemplaresValue === '' ? null : Number(exemplaresValue)
    };
}

function validateLivro(payload) {
    if (payload.titulo.length < 3 || payload.titulo.length > 150) {
        return 'O título deve ter entre 3 e 150 caracteres.';
    }
    if (!payload.autor) {
        return 'Informe o autor.';
    }
    if (!payload.isbn) {
        return 'Informe o ISBN.';
    }
    if (payload.exemplares === null || !Number.isInteger(payload.exemplares) || payload.exemplares < 0) {
        return 'Informe uma quantidade inteira de exemplares igual ou maior que zero.';
    }
    return null;
}

async function fetchLivros() {
    try {
        const response = await fetch(apiUrl);
        if (!response.ok) {
            throw new Error('Erro ao carregar livros');
        }
        const livros = await response.json();
        renderLivros(livros);
    } catch (error) {
        setStatus(error.message, 'error');
    }
}

function renderLivros(livros) {
    tbody.innerHTML = '';

    if (!livros.length) {
        tbody.innerHTML = '<tr><td colspan="6">Nenhum livro cadastrado.</td></tr>';
        return;
    }

    livros.forEach((livro) => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${livro.id}</td>
            <td>${livro.titulo}</td>
            <td>${livro.autor}</td>
            <td>${livro.isbn}</td>
            <td>${livro.exemplares}</td>
            <td>
                <button class="edit" data-id="${livro.id}">Editar</button>
                <button class="delete" data-id="${livro.id}">Excluir</button>
            </td>
        `;
        tbody.appendChild(tr);
    });

    document.querySelectorAll('button.edit').forEach((btn) => {
        btn.addEventListener('click', () => preencherFormulario(btn.dataset.id));
    });

    document.querySelectorAll('button.delete').forEach((btn) => {
        btn.addEventListener('click', () => excluirLivro(btn.dataset.id));
    });
}

async function salvarLivro(event) {
    event.preventDefault();
    const payload = getLivroPayload();
    const validationMessage = validateLivro(payload);
    if (validationMessage) {
        setStatus(validationMessage, 'error');
        return;
    }

    const id = livroIdInput.value;
    const method = id ? 'PUT' : 'POST';
    const url = id ? `${apiUrl}/${id}` : apiUrl;

    submitBtn.disabled = true;
    setStatus('Salvando livro...', 'success');
    try {
        const response = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await response.json().catch(() => null);

        if (!response.ok) {
            const details = data && data.details
                ? Object.values(data.details).join(' ')
                : '';
            const message = data && data.error
                ? `${data.error}${details ? ': ' + details : ''}`
                : 'Erro ao salvar livro';
            throw new Error(message);
        }

        setStatus(id ? 'Livro atualizado com sucesso.' : 'Livro cadastrado com sucesso.', 'success');
        form.reset();
        livroIdInput.value = '';
        submitBtn.textContent = 'Salvar';
        await fetchLivros();
    } catch (error) {
        const message = error instanceof TypeError
            ? 'Não foi possível comunicar com a API. Verifique se o backend está ativo.'
            : error.message;
        setStatus(message, 'error');
    } finally {
        submitBtn.disabled = false;
    }
}

async function preencherFormulario(id) {
    const response = await fetch(`${apiUrl}/${id}`);
    const livro = await response.json();
    livroIdInput.value = livro.id;
    document.getElementById('titulo').value = livro.titulo;
    document.getElementById('autor').value = livro.autor;
    document.getElementById('isbn').value = livro.isbn;
    document.getElementById('exemplares').value = livro.exemplares;
    submitBtn.textContent = 'Atualizar';
    setStatus('Editando livro.', 'success');
}

async function excluirLivro(id) {
    if (!confirm('Deseja excluir este livro?')) return;

    try {
        const response = await fetch(`${apiUrl}/${id}`, { method: 'DELETE' });
        if (!response.ok) {
            throw new Error('Erro ao excluir livro');
        }
        setStatus('Livro excluído com sucesso.', 'success');
        form.reset();
        livroIdInput.value = '';
        submitBtn.textContent = 'Salvar';
        await fetchLivros();
    } catch (error) {
        setStatus(error.message, 'error');
    }
}

cancelBtn.addEventListener('click', () => {
    form.reset();
    livroIdInput.value = '';
    submitBtn.textContent = 'Salvar';
    setStatus('', 'success');
});

form.addEventListener('submit', salvarLivro);
fetchLivros();
