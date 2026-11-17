package com.example.bibliotech.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.bibliotech.model.Livro;
import com.example.bibliotech.repository.LivroRepository;

@Service
public class LivroService {

    private final LivroRepository livroRepository;

    public LivroService(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    public List<Livro> listarTodos() {
        return livroRepository.findAll();
    }

    public Optional<Livro> buscarPorId(Long id) {
        return livroRepository.findById(id);
    }

    public Livro salvar(Livro livro) {
        validarIsbnDuplicado(livro.getIsbn(), null);
        return livroRepository.save(livro);
    }

    public Livro atualizar(Long id, Livro livroAtualizado) {
        Livro livroExistente = livroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Livro não encontrado."));

        validarIsbnDuplicado(livroAtualizado.getIsbn(), id);

        livroExistente.setTitulo(livroAtualizado.getTitulo());
        livroExistente.setAutor(livroAtualizado.getAutor());
        livroExistente.setIsbn(livroAtualizado.getIsbn());
        livroExistente.setExemplares(livroAtualizado.getExemplares());

        return livroRepository.save(livroExistente);
    }

    public void deletar(Long id) {
        if (!livroRepository.existsById(id)) {
            throw new IllegalArgumentException("Livro não encontrado.");
        }
        livroRepository.deleteById(id);
    }

    private void validarIsbnDuplicado(String isbn, Long idIgnorado) {
        Optional<Livro> livro = livroRepository.findByIsbn(isbn);
        if (livro.isPresent() && (idIgnorado == null || !livro.get().getId().equals(idIgnorado))) {
            throw new IllegalArgumentException("Já existe um livro com este ISBN.");
        }
    }
}
