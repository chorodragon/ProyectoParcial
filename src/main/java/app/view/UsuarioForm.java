// src/main/java/app/view/UsuarioForm.java
package app.view;

import app.core.ValidationUtil;
import app.dao.UsuarioDAO;
import app.model.Usuario;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UsuarioForm {
    public JPanel panelPrincipal;
    private JTextField txtUsername;
    private JTextField txtNombre;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmarPassword;
    private JComboBox<String> cboRol;
    private JComboBox<String> cboEstado;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnCargar;
    private JTable tblUsuarios;
    private JCheckBox chkCambiarPassword;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Username", "Nombre", "Rol", "Estado"}, 0
    );

    private Integer selectedId = null;

    public UsuarioForm() {
        initializeComponents();
        setupEventListeners();
        cargarTabla();
    }

    private void initializeComponents() {
        panelPrincipal = new JPanel(new BorderLayout());
        
        // Panel de formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtUsername = new JTextField(20);
        formPanel.add(txtUsername, gbc);
        
        // Nombre
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNombre = new JTextField(20);
        formPanel.add(txtNombre, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtPassword = new JPasswordField(20);
        formPanel.add(txtPassword, gbc);
        
        // Confirmar Password
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Confirmar Password:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtConfirmarPassword = new JPasswordField(20);
        formPanel.add(txtConfirmarPassword, gbc);
        
        // Checkbox cambiar password (para actualización)
        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.WEST;
        chkCambiarPassword = new JCheckBox("Cambiar contraseña");
        chkCambiarPassword.setVisible(false);
        formPanel.add(chkCambiarPassword, gbc);
        
        // Rol
        gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Rol:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cboRol = new JComboBox<>(new String[]{"ADMIN", "OPERADOR"});
        formPanel.add(cboRol, gbc);
        
        // Estado
        gbc.gridx = 0; gbc.gridy = 6; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Estado:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cboEstado = new JComboBox<>(new String[]{"1 - Activo", "0 - Inactivo"});
        formPanel.add(cboEstado, gbc);
        
        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnGuardar = new JButton("Guardar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnCargar = new JButton("Cargar");
        
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnActualizar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnCargar);
        
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        // Tabla
        tblUsuarios = new JTable(model);
        tblUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tblUsuarios);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        
        panelPrincipal.add(formPanel, BorderLayout.NORTH);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        
        panelPrincipal.setPreferredSize(new Dimension(700, 600));
    }

    private void setupEventListeners() {
        btnGuardar.addActionListener(e -> onGuardar());
        btnActualizar.addActionListener(e -> onActualizar());
        btnEliminar.addActionListener(e -> onEliminar());
        btnCargar.addActionListener(e -> cargarTabla());
        
        tblUsuarios.getSelectionModel().addListSelectionListener(this::onTableSelection);
        
        chkCambiarPassword.addActionListener(e -> {
            boolean cambiar = chkCambiarPassword.isSelected();
            txtPassword.setEnabled(cambiar);
            txtConfirmarPassword.setEnabled(cambiar);
            if (!cambiar) {
                txtPassword.setText("");
                txtConfirmarPassword.setText("");
            }
        });
    }

    private void onTableSelection(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        int row = tblUsuarios.getSelectedRow();
        if (row == -1) {
            selectedId = null;
            modoCreacion();
            return;
        }
        
        selectedId = Integer.parseInt(model.getValueAt(row, 0).toString());
        txtUsername.setText(model.getValueAt(row, 1).toString());
        txtNombre.setText(model.getValueAt(row, 2).toString());
        
        String rol = model.getValueAt(row, 3).toString();
        cboRol.setSelectedItem(rol);
        
        String estTxt = model.getValueAt(row, 4).toString();
        cboEstado.setSelectedIndex("Activo".equalsIgnoreCase(estTxt) ? 0 : 1);
        
        modoActualizacion();
    }

    private void modoCreacion() {
        chkCambiarPassword.setVisible(false);
        txtPassword.setEnabled(true);
        txtConfirmarPassword.setEnabled(true);
        btnGuardar.setVisible(true);
        btnActualizar.setVisible(false);
        btnEliminar.setVisible(false);
    }

    private void modoActualizacion() {
        chkCambiarPassword.setVisible(true);
        chkCambiarPassword.setSelected(false);
        txtPassword.setEnabled(false);
        txtConfirmarPassword.setEnabled(false);
        txtPassword.setText("");
        txtConfirmarPassword.setText("");
        btnGuardar.setVisible(false);
        btnActualizar.setVisible(true);
        btnEliminar.setVisible(true);
    }

    private void onGuardar() {
        String username = txtUsername.getText().trim();
        String nombre = txtNombre.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmarPassword = new String(txtConfirmarPassword.getPassword());
        String rol = (String) cboRol.getSelectedItem();
        
        // Validaciones
        String error = ValidationUtil.validateRequired(username, "Username");
        if (error != null) {
            ValidationUtil.showError(error);
            txtUsername.requestFocus();
            return;
        }
        
        error = ValidationUtil.validateRequired(nombre, "Nombre");
        if (error != null) {
            ValidationUtil.showError(error);
            txtNombre.requestFocus();
            return;
        }
        
        error = ValidationUtil.validateRequired(password, "Password");
        if (error != null) {
            ValidationUtil.showError(error);
            txtPassword.requestFocus();
            return;
        }
        
        error = ValidationUtil.validatePasswordMatch(password, confirmarPassword);
        if (error != null) {
            ValidationUtil.showError(error);
            txtConfirmarPassword.requestFocus();
            return;
        }

        try {
            // Verificar si ya existe el username
            if (usuarioDAO.existeUsername(username)) {
                ValidationUtil.showError("El username ya existe");
                txtUsername.requestFocus();
                return;
            }
            
            int id = usuarioDAO.crearUsuario(username, password, nombre, rol);
            if (id > 0) {
                ValidationUtil.showSuccess("Usuario guardado exitosamente");
                limpiarFormulario();
                cargarTabla();
            } else {
                ValidationUtil.showError("No se pudo guardar el usuario");
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al guardar el usuario: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void onActualizar() {
        if (selectedId == null) {
            ValidationUtil.showError("Seleccione un usuario para actualizar");
            return;
        }
        
        String username = txtUsername.getText().trim();
        String nombre = txtNombre.getText().trim();
        String rol = (String) cboRol.getSelectedItem();
        int estado = (cboEstado.getSelectedIndex() == 0) ? 1 : 0;
        
        // Validaciones
        String error = ValidationUtil.validateRequired(username, "Username");
        if (error != null) {
            ValidationUtil.showError(error);
            txtUsername.requestFocus();
            return;
        }
        
        error = ValidationUtil.validateRequired(nombre, "Nombre");
        if (error != null) {
            ValidationUtil.showError(error);
            txtNombre.requestFocus();
            return;
        }
        
        // Verificar si el username ya existe (excepto el actual)
        try {
            if (usuarioDAO.existeUsernameExceptoId(username, selectedId)) {
                ValidationUtil.showError("El username ya existe");
                txtUsername.requestFocus();
                return;
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al verificar username: " + ex.getMessage());
            return;
        }

        if (!ValidationUtil.showConfirmation("¿Está seguro de actualizar este usuario?")) {
            return;
        }

        try {
            boolean ok;
            
            if (chkCambiarPassword.isSelected()) {
                String password = new String(txtPassword.getPassword());
                String confirmarPassword = new String(txtConfirmarPassword.getPassword());
                
                error = ValidationUtil.validateRequired(password, "Password");
                if (error != null) {
                    ValidationUtil.showError(error);
                    txtPassword.requestFocus();
                    return;
                }
                
                error = ValidationUtil.validatePasswordMatch(password, confirmarPassword);
                if (error != null) {
                    ValidationUtil.showError(error);
                    txtConfirmarPassword.requestFocus();
                    return;
                }
                
                ok = usuarioDAO.actualizarConPassword(selectedId, username, nombre, rol, estado, password);
            } else {
                ok = usuarioDAO.actualizarSinPassword(selectedId, username, nombre, rol, estado);
            }
            
            if (ok) {
                ValidationUtil.showSuccess("Usuario actualizado exitosamente");
                cargarTabla();
                seleccionarFilaPorId(selectedId);
            } else {
                ValidationUtil.showError("No se pudo actualizar el usuario");
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al actualizar el usuario: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void onEliminar() {
        if (selectedId == null) {
            ValidationUtil.showError("Seleccione un usuario para eliminar");
            return;
        }

        if (!ValidationUtil.showConfirmation("¿Está seguro de eliminar este usuario?\nEsta acción marcará el usuario como inactivo.")) {
            return;
        }

        try {
            boolean ok = usuarioDAO.eliminarLogico(selectedId);
            if (ok) {
                ValidationUtil.showSuccess("Usuario eliminado exitosamente");
                limpiarFormulario();
                cargarTabla();
            } else {
                ValidationUtil.showError("No se pudo eliminar el usuario");
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al eliminar el usuario: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void cargarTabla() {
        try {
            List<Usuario> lista = usuarioDAO.listar();
            model.setRowCount(0);
            for (Usuario u : lista) {
                model.addRow(new Object[]{
                        u.getId(),
                        u.getUsername(),
                        u.getNombre(),
                        u.getRol(),
                        u.getEstado() == 1 ? "Activo" : "Inactivo"
                });
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al cargar usuarios: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void limpiarFormulario() {
        txtUsername.setText("");
        txtNombre.setText("");
        txtPassword.setText("");
        txtConfirmarPassword.setText("");
        cboRol.setSelectedIndex(0);
        cboEstado.setSelectedIndex(0);
        tblUsuarios.clearSelection();
        selectedId = null;
        modoCreacion();
    }

    private void seleccionarFilaPorId(Integer id) {
        if (id == null) return;
        for (int i = 0; i < model.getRowCount(); i++) {
            Object val = model.getValueAt(i, 0);
            if (val != null && Integer.parseInt(val.toString()) == id) {
                tblUsuarios.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Gestión de Usuarios");
            f.setContentPane(new UsuarioForm().panelPrincipal);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}