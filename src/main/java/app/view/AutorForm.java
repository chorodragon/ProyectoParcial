package app.view;

import app.core.ValidationUtil;
import app.dao.AutorDAO;
import app.model.Autor;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AutorForm {
    public JPanel panelPrincipal;
    private JTextField txtNombre;
    private JTextArea txtBiografia;
    private JComboBox<String> cboEstado;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnCargar;
    private JTable tblAutores;
    private JTextField txtBuscar;

    private final AutorDAO autorDAO = new AutorDAO();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Biografía", "Estado"}, 0
    );

    private Integer selectedId = null;

    public AutorForm() {
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
        
        // Nombre
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNombre = new JTextField(20);
        formPanel.add(txtNombre, gbc);
        
        // Biografía
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.NORTHEAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Biografía:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.3;
        txtBiografia = new JTextArea(4, 20);
        txtBiografia.setLineWrap(true);
        txtBiografia.setWrapStyleWord(true);
        JScrollPane scrollBio = new JScrollPane(txtBiografia);
        formPanel.add(scrollBio, gbc);
        
        // Estado
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;
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
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Buscar por nombre:"));
        txtBuscar = new JTextField(20);
        searchPanel.add(txtBuscar);
        
        // Tabla
        tblAutores = new JTable(model);
        tblAutores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tblAutores);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        panelPrincipal.add(formPanel, BorderLayout.NORTH);
        panelPrincipal.add(centerPanel, BorderLayout.CENTER);
        
        panelPrincipal.setPreferredSize(new Dimension(700, 600));
        
        // Configurar modo inicial
        modoCreacion();
    }

    private void setupEventListeners() {
        btnGuardar.addActionListener(e -> onGuardar());
        btnActualizar.addActionListener(e -> onActualizar());
        btnEliminar.addActionListener(e -> onEliminar());
        btnCargar.addActionListener(e -> cargarTabla());

        tblAutores.getSelectionModel().addListSelectionListener(this::onTableSelection);
        
        // Búsqueda en tiempo real
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscarAutores();
            }
        });
    }

    private void onTableSelection(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        int row = tblAutores.getSelectedRow();
        if (row == -1) {
            selectedId = null;
            modoCreacion();
            return;
        }

        selectedId = Integer.parseInt(model.getValueAt(row, 0).toString());
        txtNombre.setText(model.getValueAt(row, 1).toString());
        txtBiografia.setText(model.getValueAt(row, 2) != null ? model.getValueAt(row, 2).toString() : "");

        String estTxt = model.getValueAt(row, 3).toString();
        cboEstado.setSelectedIndex("Activo".equalsIgnoreCase(estTxt) ? 0 : 1);
        
        modoActualizacion();
    }

    private void modoCreacion() {
        btnGuardar.setVisible(true);
        btnActualizar.setVisible(false);
        btnEliminar.setVisible(false);
    }

    private void modoActualizacion() {
        btnGuardar.setVisible(false);
        btnActualizar.setVisible(true);
        btnEliminar.setVisible(true);
    }

    private void onGuardar() {
        String nombre = txtNombre.getText().trim();
        String biografia = txtBiografia.getText().trim();
        int estado = (cboEstado.getSelectedIndex() == 0) ? 1 : 0;

        // Validaciones
        String error = ValidationUtil.validateRequired(nombre, "Nombre");
        if (error != null) {
            ValidationUtil.showError(error);
            txtNombre.requestFocus();
            return;
        }

        try {
            Autor autor = new Autor(nombre, biografia, estado);
            int id = autorDAO.insertar(autor);
            if (id > 0) {
                ValidationUtil.showSuccess("Autor guardado exitosamente");
                limpiarFormulario();
                cargarTabla();
            } else {
                ValidationUtil.showError("No se pudo guardar el autor");
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al guardar el autor: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void onActualizar() {
        if (selectedId == null) {
            ValidationUtil.showError("Seleccione un autor para actualizar");
            return;
        }

        String nombre = txtNombre.getText().trim();
        String biografia = txtBiografia.getText().trim();
        int estado = (cboEstado.getSelectedIndex() == 0) ? 1 : 0;

        // Validaciones
        String error = ValidationUtil.validateRequired(nombre, "Nombre");
        if (error != null) {
            ValidationUtil.showError(error);
            txtNombre.requestFocus();
            return;
        }

        if (!ValidationUtil.showConfirmation("¿Está seguro de actualizar este autor?")) {
            return;
        }

        try {
            Autor autor = new Autor(selectedId, nombre, biografia, estado);
            boolean ok = autorDAO.actualizar(autor);
            if (ok) {
                ValidationUtil.showSuccess("Autor actualizado exitosamente");
                cargarTabla();
                seleccionarFilaPorId(selectedId);
            } else {
                ValidationUtil.showError("No se pudo actualizar el autor");
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al actualizar el autor: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void onEliminar() {
        if (selectedId == null) {
            ValidationUtil.showError("Seleccione un autor para eliminar");
            return;
        }

        if (!ValidationUtil.showConfirmation("¿Está seguro de eliminar este autor?\nEsta acción marcará el autor como inactivo.")) {
            return;
        }

        try {
            boolean ok = autorDAO.eliminarLogico(selectedId);
            if (ok) {
                ValidationUtil.showSuccess("Autor eliminado exitosamente");
                limpiarFormulario();
                cargarTabla();
            } else {
                ValidationUtil.showError("No se pudo eliminar el autor");
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al eliminar el autor: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void cargarTabla() {
        try {
            List<Autor> lista = autorDAO.listar();
            model.setRowCount(0);
            for (Autor autor : lista) {
                model.addRow(new Object[]{
                        autor.getId(),
                        autor.getNombre(),
                        autor.getBiografia(),
                        autor.getEstado() == 1 ? "Activo" : "Inactivo"
                });
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al cargar autores: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void buscarAutores() {
        String termino = txtBuscar.getText().trim();
        if (termino.isEmpty()) {
            cargarTabla();
            return;
        }
        
        try {
            List<Autor> lista = autorDAO.buscarPorNombre(termino);
            model.setRowCount(0);
            for (Autor autor : lista) {
                model.addRow(new Object[]{
                        autor.getId(),
                        autor.getNombre(),
                        autor.getBiografia(),
                        autor.getEstado() == 1 ? "Activo" : "Inactivo"
                });
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al buscar autores: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtBiografia.setText("");
        cboEstado.setSelectedIndex(0);
        tblAutores.clearSelection();
        txtBuscar.setText("");
        selectedId = null;
        modoCreacion();
    }

    private void seleccionarFilaPorId(Integer id) {
        if (id == null) return;
        for (int i = 0; i < model.getRowCount(); i++) {
            Object val = model.getValueAt(i, 0);
            if (val != null && Integer.parseInt(val.toString()) == id) {
                tblAutores.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Gestión de Autores");
            f.setContentPane(new AutorForm().panelPrincipal);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}