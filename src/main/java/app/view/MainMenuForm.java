// src/main/java/app/view/MainMenuForm.java - Versión completa
package app.view;

import app.core.Sesion;

import javax.swing.*;
import java.awt.*;

public class MainMenuForm {
    public JPanel panelPrincipal;
    private JButton btnAutores;
    private JButton btnLibros;
    private JButton btnUsuarios;
    private JButton btnCategorias;
    private JButton btnClientes;
    private JButton btnPrestamos;
    private JButton btnSalir;
    private JLabel lblUsuario;

    public MainMenuForm() {
        initializeComponents();
        setupEventListeners();
        configurarPermisosPorRol();
    }

    private void initializeComponents() {
        panelPrincipal = new JPanel(new BorderLayout());
        
        // Panel de información del usuario
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblUsuario = new JLabel();
        if (Sesion.isLogged()) {
            lblUsuario.setText("Usuario: " + Sesion.getUsuario().getNombre() + 
                             " (" + Sesion.getUsuario().getRol() + ")");
            lblUsuario.setFont(lblUsuario.getFont().deriveFont(Font.BOLD));
        }
        userPanel.add(lblUsuario);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new GridLayout(7, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        btnAutores = new JButton("Gestión de Autores");
        btnLibros = new JButton("Gestión de Libros");
        btnUsuarios = new JButton("Gestión de Usuarios");
        btnCategorias = new JButton("Gestión de Categorías");
        btnClientes = new JButton("Gestión de Clientes");
        btnPrestamos = new JButton("Gestión de Préstamos");
        btnSalir = new JButton("Salir");
        
        // Configurar tamaño de botones
        Dimension buttonSize = new Dimension(250, 40);
        btnAutores.setPreferredSize(buttonSize);
        btnLibros.setPreferredSize(buttonSize);
        btnUsuarios.setPreferredSize(buttonSize);
        btnCategorias.setPreferredSize(buttonSize);
        btnClientes.setPreferredSize(buttonSize);
        btnPrestamos.setPreferredSize(buttonSize);
        btnSalir.setPreferredSize(buttonSize);
        
        buttonPanel.add(btnAutores);
        buttonPanel.add(btnLibros);
        buttonPanel.add(btnUsuarios);
        buttonPanel.add(btnCategorias);
        buttonPanel.add(btnClientes);
        buttonPanel.add(btnPrestamos);
        buttonPanel.add(btnSalir);
        
        // Título
        JLabel titulo = new JLabel("Sistema de Biblioteca", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        panelPrincipal.add(titulo, BorderLayout.NORTH);
        panelPrincipal.add(userPanel, BorderLayout.NORTH);
        panelPrincipal.add(buttonPanel, BorderLayout.CENTER);
        
        panelPrincipal.setPreferredSize(new Dimension(400, 500));
    }

    private void setupEventListeners() {
        btnAutores.addActionListener(e -> abrirAutores());
        btnLibros.addActionListener(e -> abrirLibros());
        btnUsuarios.addActionListener(e -> abrirUsuarios());
        btnCategorias.addActionListener(e -> abrirCategorias());
        btnClientes.addActionListener(e -> abrirClientes());
        btnPrestamos.addActionListener(e -> abrirPrestamos());
        btnSalir.addActionListener(e -> salir());
    }

    private void configurarPermisosPorRol() {
        if (!Sesion.isLogged()) {
            // Si no hay usuario logueado, deshabilitar todo
            deshabilitarTodo();
            return;
        }
        
        String rol = Sesion.getUsuario().getRol();
        
        if ("ADMIN".equalsIgnoreCase(rol)) {
            // ADMIN tiene acceso a todo
            habilitarTodo();
        } else if ("OPERADOR".equalsIgnoreCase(rol)) {
            // OPERADOR solo tiene acceso limitado
            btnAutores.setEnabled(true);
            btnLibros.setEnabled(true);
            btnClientes.setEnabled(true);
            btnPrestamos.setEnabled(true);
            
            // No puede acceder a usuarios ni categorías - se muestran en gris
            btnUsuarios.setEnabled(false);
            btnCategorias.setEnabled(false);
            
            btnSalir.setEnabled(true);
        } else {
            // Rol desconocido, deshabilitar todo por seguridad
            deshabilitarTodo();
        }
    }

    private void habilitarTodo() {
        btnAutores.setEnabled(true);
        btnLibros.setEnabled(true);
        btnUsuarios.setEnabled(true);
        btnCategorias.setEnabled(true);
        btnClientes.setEnabled(true);
        btnPrestamos.setEnabled(true);
        btnSalir.setEnabled(true);
    }

    private void deshabilitarTodo() {
        btnAutores.setEnabled(false);
        btnLibros.setEnabled(false);
        btnUsuarios.setEnabled(false);
        btnCategorias.setEnabled(false);
        btnClientes.setEnabled(false);
        btnPrestamos.setEnabled(false);
        btnSalir.setEnabled(true); // Siempre permitir salir
    }

    private void abrirAutores() {
        abrirFormulario("Gestión de Autores", new AutorForm().panelPrincipal);
    }

    private void abrirLibros() {
        abrirFormulario("Gestión de Libros", new LibroForm().panelPrincipal);
    }

    private void abrirUsuarios() {
        if (!Sesion.hasRole("ADMIN")) {
            JOptionPane.showMessageDialog(panelPrincipal, 
                "No tiene permisos para acceder a la gestión de usuarios", 
                "Acceso Denegado", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        abrirFormulario("Gestión de Usuarios", new UsuarioForm().panelPrincipal);
    }

    private void abrirCategorias() {
        if (!Sesion.hasRole("ADMIN")) {
            JOptionPane.showMessageDialog(panelPrincipal, 
                "No tiene permisos para acceder a la gestión de categorías", 
                "Acceso Denegado", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        abrirFormulario("Gestión de Categorías", new CategoriaForm().panelPrincipal);
    }

    private void abrirClientes() {
        abrirFormulario("Gestión de Clientes", new ClienteForm().panelPrincipal);
    }

    private void abrirPrestamos() {
        abrirFormulario("Gestión de Préstamos", new PrestamoForm().panelPrincipal);
    }

    private void abrirFormulario(String titulo, JPanel panel) {
        JFrame frame = new JFrame(titulo);
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(panelPrincipal);
        frame.setVisible(true);
    }

    private void salir() {
        int confirmacion = JOptionPane.showConfirmDialog(
            panelPrincipal,
            "¿Está seguro que desea salir del sistema?",
            "Confirmar Salida",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            Sesion.logout();
            System.exit(0);
        }
    }
}