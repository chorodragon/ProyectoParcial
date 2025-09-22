// src/main/java/app/view/PrestamoForm.java
package app.view;

import app.core.ValidationUtil;
import app.dao.ClienteDAO;
import app.dao.LibroDAO;
import app.dao.PrestamoDAO;
import app.model.Cliente;
import app.model.ComboItem;
import app.model.LibroConAutor;
import app.model.Prestamo;
import app.model.PrestamoDetalle;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class PrestamoForm {
    public JPanel panelPrincipal;
    private JComboBox<ComboItem> cboCliente;
    private JComboBox<ComboItem> cboLibro;
    private JTextField txtFecha;
    private JComboBox<String> cboEstado;
    private JButton btnPrestar;
    private JButton btnDevolver;
    private JButton btnCargar;
    private JTable tblPrestamos;
    private JButton btnCargarLibros;
    private JCheckBox chkSoloActivos;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final LibroDAO libroDAO = new LibroDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Cliente", "Libro", "Fecha", "Estado"}, 0
    );

    private Integer selectedId = null;

    public PrestamoForm() {
        initializeComponents();
        setupEventListeners();
        cargarClientes();
        cargarLibros();
        cargarTabla();
    }

    private void initializeComponents() {
        panelPrincipal = new JPanel(new BorderLayout());
        
        // Panel de formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Cliente
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Cliente:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cboCliente = new JComboBox<>();
        formPanel.add(cboCliente, gbc);
        
        // Libro
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Libro:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cboLibro = new JComboBox<>();
        formPanel.add(cboLibro, gbc);
        
        // Botón cargar libros
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        btnCargarLibros = new JButton("Actualizar Libros");
        formPanel.add(btnCargarLibros, gbc);
        
        // Fecha
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Fecha:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtFecha = new JTextField(LocalDate.now().toString());
        txtFecha.setEditable(false);
        formPanel.add(txtFecha, gbc);
        
        // Estado (para mostrar información)
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Estado:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cboEstado = new JComboBox<>(new String[]{"1 - Activo", "0 - Devuelto"});
        cboEstado.setEnabled(false);
        formPanel.add(cboEstado, gbc);
        
        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnPrestar = new JButton("Prestar Libro");
        btnDevolver = new JButton("Devolver Libro");
        btnCargar = new JButton("Cargar Préstamos");
        
        buttonPanel.add(btnPrestar);
        buttonPanel.add(btnDevolver);
        buttonPanel.add(btnCargar);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        formPanel.add(buttonPanel, gbc);
        
        // Panel de filtros
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        chkSoloActivos = new JCheckBox("Solo préstamos activos", true);
        filterPanel.add(chkSoloActivos);
        
        // Tabla
        tblPrestamos = new JTable(model);
        tblPrestamos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tblPrestamos);
        scrollPane.setPreferredSize(new Dimension(700, 300));
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(filterPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        panelPrincipal.add(formPanel, BorderLayout.NORTH);
        panelPrincipal.add(centerPanel, BorderLayout.CENTER);
        
        panelPrincipal.setPreferredSize(new Dimension(800, 700));
    }

    private void setupEventListeners() {
        btnPrestar.addActionListener(e -> onPrestar());
        btnDevolver.addActionListener(e -> onDevolver());
        btnCargar.addActionListener(e -> cargarTabla());
        btnCargarLibros.addActionListener(e -> cargarLibros());
        
        chkSoloActivos.addActionListener(e -> cargarTabla());
        
        tblPrestamos.getSelectionModel().addListSelectionListener(this::onTableSelection);
    }

    private void onTableSelection(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        int row = tblPrestamos.getSelectedRow();
        if (row == -1) {
            selectedId = null;
            btnDevolver.setEnabled(false);
            return;
        }
        
        selectedId = Integer.parseInt(model.getValueAt(row, 0).toString());
        String estado = model.getValueAt(row, 4).toString();
        
        // Solo se puede devolver si está activo
        btnDevolver.setEnabled("Activo".equalsIgnoreCase(estado));
        
        // Cargar datos en los campos (para información)
        String clienteNombre = model.getValueAt(row, 1).toString();
        String libroNombre = model.getValueAt(row, 2).toString();
        String fecha = model.getValueAt(row, 3).toString();
        
        seleccionarClientePorNombre(clienteNombre);
        seleccionarLibroPorNombre(libroNombre);
        txtFecha.setText(fecha);
        cboEstado.setSelectedIndex("Activo".equalsIgnoreCase(estado) ? 0 : 1);
    }

    private void onPrestar() {
        ComboItem clienteItem = (ComboItem) cboCliente.getSelectedItem();
        ComboItem libroItem = (ComboItem) cboLibro.getSelectedItem();
        
        if (clienteItem == null) {
            ValidationUtil.showError("Seleccione un cliente");
            return;
        }
        
        if (libroItem == null) {
            ValidationUtil.showError("Seleccione un libro");
            return;
        }

        try {
            // Verificar si el libro ya está prestado
            if (prestamoDAO.libroEstaPrestado(libroItem.getId())) {
                ValidationUtil.showError("El libro seleccionado ya está prestado actualmente");
                return;
            }
            
            if (!ValidationUtil.showConfirmation("¿Confirma el préstamo del libro \"" + libroItem.getLabel() + "\" al cliente \"" + clienteItem.getLabel() + "\"?")) {
                return;
            }

            Date fechaActual = Date.valueOf(LocalDate.now());
            Prestamo prestamo = new Prestamo(clienteItem.getId(), libroItem.getId(), fechaActual, 1);
            
            int id = prestamoDAO.insertar(prestamo);
            if (id > 0) {
                ValidationUtil.showSuccess("Préstamo registrado exitosamente");
                limpiarFormulario();
                cargarTabla();
                cargarLibros(); // Actualizar lista de libros disponibles
            } else {
                ValidationUtil.showError("No se pudo registrar el préstamo");
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al registrar el préstamo: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void onDevolver() {
        if (selectedId == null) {
            ValidationUtil.showError("Seleccione un préstamo activo para devolver");
            return;
        }

        String libroNombre = model.getValueAt(tblPrestamos.getSelectedRow(), 2).toString();
        String clienteNombre = model.getValueAt(tblPrestamos.getSelectedRow(), 1).toString();
        
        if (!ValidationUtil.showConfirmation("¿Confirma la devolución del libro \"" + libroNombre + "\" del cliente \"" + clienteNombre + "\"?")) {
            return;
        }

        try {
            boolean ok = prestamoDAO.devolverPrestamo(selectedId);
            if (ok) {
                ValidationUtil.showSuccess("Libro devuelto exitosamente");
                limpiarFormulario();
                cargarTabla();
                cargarLibros(); // Actualizar lista de libros disponibles
            } else {
                ValidationUtil.showError("No se pudo procesar la devolución");
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al procesar la devolución: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void cargarClientes() {
        try {
            cboCliente.removeAllItems();
            List<Cliente> clientes = clienteDAO.listarActivos();
            for (Cliente c : clientes) {
                cboCliente.addItem(new ComboItem(c.getId(), c.getNombre()));
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al cargar clientes: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void cargarLibros() {
        try {
            cboLibro.removeAllItems();
            List<LibroConAutor> libros = libroDAO.listarActivosConAutorYCategoria();
            
            for (LibroConAutor l : libros) {
                // Solo mostrar libros que no estén prestados actualmente
                if (!prestamoDAO.libroEstaPrestado(l.getId())) {
                    String label = l.getNombre() + " - " + l.getAutorNombre();
                    cboLibro.addItem(new ComboItem(l.getId(), label));
                }
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al cargar libros: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void cargarTabla() {
        try {
            List<PrestamoDetalle> lista;
            if (chkSoloActivos.isSelected()) {
                lista = prestamoDAO.listarActivos();
            } else {
                lista = prestamoDAO.listarConDetalles();
            }
            
            model.setRowCount(0);
            for (PrestamoDetalle p : lista) {
                model.addRow(new Object[]{
                        p.getId(),
                        p.getClienteNombre(),
                        p.getLibroNombre(),
                        p.getFecha(),
                        p.getEstado() == 1 ? "Activo" : "Devuelto"
                });
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al cargar préstamos: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void limpiarFormulario() {
        if (cboCliente.getItemCount() > 0) cboCliente.setSelectedIndex(0);
        if (cboLibro.getItemCount() > 0) cboLibro.setSelectedIndex(0);
        txtFecha.setText(LocalDate.now().toString());
        cboEstado.setSelectedIndex(0);
        tblPrestamos.clearSelection();
        selectedId = null;
        btnDevolver.setEnabled(false);
    }

    private void seleccionarClientePorNombre(String nombre) {
        for (int i = 0; i < cboCliente.getItemCount(); i++) {
            ComboItem item = cboCliente.getItemAt(i);
            if (item.getLabel().equalsIgnoreCase(nombre)) {
                cboCliente.setSelectedIndex(i);
                return;
            }
        }
    }

    private void seleccionarLibroPorNombre(String nombre) {
        for (int i = 0; i < cboLibro.getItemCount(); i++) {
            ComboItem item = cboLibro.getItemAt(i);
            if (item.getLabel().contains(nombre)) {
                cboLibro.setSelectedIndex(i);
                return;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Gestión de Préstamos");
            f.setContentPane(new PrestamoForm().panelPrincipal);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}