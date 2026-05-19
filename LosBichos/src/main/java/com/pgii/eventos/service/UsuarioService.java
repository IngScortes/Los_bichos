package com.pgii.eventos.service;

import com.pgii.eventos.model.Usuario;
import com.pgii.eventos.repository.UsuarioRepository;

import java.util.List;

public class UsuarioService {
    private UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public boolean registrarUsuario(String id, String nombre, String email, String telefono) {
        if (usuarioRepository.findById(id) != null) {
            return false; // ID ya existe
        }
        Usuario nuevo = new Usuario(id, nombre, email, telefono);
        usuarioRepository.save(nuevo);
        return true;
    }

    public Usuario login(String email, String telefono) {
        // Búsqueda simple por email o teléfono
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getEmail().equals(email) || u.getTelefono().equals(telefono))
                .findFirst()
                .orElse(null);
    }

    public Usuario buscarPorId(String id) {
        return usuarioRepository.findById(id);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public boolean actualizarPerfil(String id, String nombre, String email, String telefono) {
        Usuario u = usuarioRepository.findById(id);
        if (u == null) return false;
        u.setNombreCompleto(nombre);
        u.setEmail(email);
        u.setTelefono(telefono);
        usuarioRepository.save(u);
        return true;
    }

    public void agregarMetodoPago(String idUsuario, String metodo) {
        Usuario u = usuarioRepository.findById(idUsuario);
        if (u != null) {
            u.agregarMetodoPago(metodo);
            usuarioRepository.save(u);
        }
    }
}