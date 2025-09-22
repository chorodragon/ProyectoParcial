package app.view;

import app.core.Sesion;

import javax.swing.*;
import java.awt.*;

public class MainMenuForm {
    public JPanel panelPrincipal;
    private JButton btnAutores;
    private JButton btnLibros;
    private JButton btnSalir;
    private JLabel lblUsuario;

    public MainMenuForm() {
        panelPrincipal.setPreferredSize(new Dimension(420, 240));

        if (lblUsuario != null && Sesion.isLogged()) {
            lblUsuario.setText("Usuario: " + Sesion.getUsuario().getNombre()
                    + " (" + Sesion.getUsuario().getRol() + ")");
        }

        // Ejemplo de permiso por rol
        if (btnAutores != null) {
            btnAutores.setEnabled(Sesion.hasRole("ADMIN"));
        }

        if (btnAutores != null) btnAutores.addActionListener(e -> abrirAutores());
        if (btnLibros  != null) btnLibros.addActionListener(e -> abrirLibros());
        if (btnSalir   != null) btnSalir.addActionListener(e -> {
            Sesion.logout();
            System.exit(0);
        });
    }

    private void abrirAutores() {
        JFrame f = new JFrame("Gestión de Autores");
        f.setContentPane(new AutorForm().panelPrincipal);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.pack(); f.setLocationRelativeTo(null); f.setVisible(true);
    }
    private void abrirLibros() {
        JFrame f = new JFrame("Gestión de Libros");
        f.setContentPane(new LibroForm().panelPrincipal);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.pack(); f.setLocationRelativeTo(null); f.setVisible(true);
    }
}