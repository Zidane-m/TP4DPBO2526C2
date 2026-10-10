import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class PeopleMenu extends JFrame {
    public static void main(String[] args) {
        // buat object window
        PeopleMenu menu = new PeopleMenu();

        // atur ukuran window
        menu.setSize(700, 600);

        // letakkan window di tengah layar
        menu.setLocationRelativeTo(null);

        // isi window
        menu.setContentPane(menu.mainPanel);

        // ubah warna background
        menu.getContentPane().setBackground(Color.WHITE);

        // tampilkan window
        menu.setVisible(true);

        // agar program ikut berhenti saat window diclose
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // index baris yang diklik
    private int selectedIndex = -1;
    // list untuk menampung semua data orang
    private ArrayList<Person> listPerson;

    private JPanel mainPanel;
    private JTextField idField;
    private JTextField namaField;
    private JTextField tahunLahirField;
    private JTable peopleTable;
    private JButton addUpdateButton;
    private JButton cancelButton;
    private JComboBox<String> kategoriComboBox;
    private JButton deleteButton;
    private JLabel titleLabel;
    private JLabel idLabel;
    private JLabel namaLabel;
    private JLabel tahunLahirLabel;
    private JLabel kategoriLabel;
    private JLabel shiftLabel;
    private JPanel shiftPanel;
    private JRadioButton pagiRadioButton;
    private JRadioButton siangRadioButton;
    private JRadioButton malamRadioButton;
    private ButtonGroup shiftButtonGroup;

    // constructor
    public PeopleMenu() {
        // inisialisasi listPerson
        listPerson = new ArrayList<>();

        // isi listPerson
        populateList();

        // isi tabel orang
        peopleTable.setModel(setTable());

        // ubah styling title
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));

        // atur isi combo box kategori
        String[] kategoriData = { "???", "Dokter Spesialis", "Dokter Umum", "Perawat", "Apoteker", "Staf Administrasi" };
        kategoriComboBox.setModel(new DefaultComboBoxModel<>(kategoriData));

        // kelompokkan radio button shift kerja
        shiftButtonGroup = new ButtonGroup();
        shiftButtonGroup.add(pagiRadioButton);
        shiftButtonGroup.add(siangRadioButton);
        shiftButtonGroup.add(malamRadioButton);
        pagiRadioButton.setSelected(true);

        // sesuaikan background panel radio button agar menyatu dengan frame putih
        if (shiftPanel != null) {
            shiftPanel.setOpaque(false);
        }
        pagiRadioButton.setOpaque(false);
        siangRadioButton.setOpaque(false);
        malamRadioButton.setOpaque(false);

        // sembunyikan button delete
        deleteButton.setVisible(false);

        // saat tombol add/update ditekan
        addUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex == -1) {
                    insertData();
                } else {
                    updateData();
                }
            }
        });

        // saat tombol delete ditekan
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex >= 0) {
                    int confirm = JOptionPane.showConfirmDialog(
                            null,
                            "Apakah Anda yakin ingin menghapus data ini?",
                            "Konfirmasi Hapus",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteData();
                    }
                }
            }
        });

        // saat tombol cancel ditekan
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });

        // saat salah satu baris tabel ditekan
        peopleTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // ubah selectedIndex menjadi baris tabel yang diklik
                selectedIndex = peopleTable.getSelectedRow();

                // simpan value textfield, combo box, dan radio button
                String curId = peopleTable.getModel().getValueAt(selectedIndex, 1).toString();
                String curNama = peopleTable.getModel().getValueAt(selectedIndex, 2).toString();
                String curTahunLahir = peopleTable.getModel().getValueAt(selectedIndex, 3).toString();
                String curKategori = peopleTable.getModel().getValueAt(selectedIndex, 4).toString();
                String curShift = peopleTable.getModel().getValueAt(selectedIndex, 5).toString();

                // ubah isi textfield, combo box, dan radio button
                idField.setText(curId);
                namaField.setText(curNama);
                tahunLahirField.setText(curTahunLahir);
                kategoriComboBox.setSelectedItem(curKategori);
                setSelectedShift(curShift);

                // ubah button "Add" menjadi "Update"
                addUpdateButton.setText("Update");

                // tampilkan button delete
                deleteButton.setVisible(true);
            }
        });
    }

    // mengambil nilai shift kerja dari JRadioButton yang dipilih
    private String getSelectedShift() {
        if (siangRadioButton.isSelected()) {
            return "Siang";
        } else if (malamRadioButton.isSelected()) {
            return "Malam";
        }
        return "Pagi";
    }

    // mengatur pilihan JRadioButton berdasarkan nilai shift kerja
    private void setSelectedShift(String shift) {
        if ("Siang".equalsIgnoreCase(shift)) {
            siangRadioButton.setSelected(true);
        } else if ("Malam".equalsIgnoreCase(shift)) {
            malamRadioButton.setSelected(true);
        } else {
            pagiRadioButton.setSelected(true);
        }
    }

    // setTable
    public final DefaultTableModel setTable() {
        // tentukan kolom tabel
        Object[] cols = { "No", "ID", "Nama", "Tahun Lahir", "Kategori", "Shift Kerja" };

        // buat objek tabel dengan kolom yang sudah dibuat
        DefaultTableModel tmp = new DefaultTableModel(null, cols);

        // isi tabel dengan listPerson
        for (int i = 0; i < listPerson.size(); i++) {
            Object[] row = { i + 1,
                    listPerson.get(i).getId(),
                    listPerson.get(i).getNama(),
                    listPerson.get(i).getTahunLahir(),
                    listPerson.get(i).getKategori(),
                    listPerson.get(i).getShiftKerja()
            };
            tmp.addRow(row);
        }

        return tmp;
    }

    // memeriksa apakah ID sudah ada di dalam list (excludeIndex untuk mengabaikan baris sendiri saat update)
    private boolean isIdExist(String id, int excludeIndex) {
        for (int i = 0; i < listPerson.size(); i++) {
            if (i != excludeIndex && listPerson.get(i).getId().equalsIgnoreCase(id.trim())) {
                return true;
            }
        }
        return false;
    }

    // insertData
    public void insertData() {
        // ambil value dari form
        String id = idField.getText().trim();
        String nama = namaField.getText().trim();
        String tahunLahirStr = tahunLahirField.getText().trim();
        String kategori = kategoriComboBox.getSelectedItem().toString();
        String shiftKerja = getSelectedShift();

        // validasi field tidak boleh kosong
        if (id.isEmpty() || nama.isEmpty() || tahunLahirStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Semua field harus diisi!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // validasi kategori harus dipilih
        if ("???".equals(kategori)) {
            JOptionPane.showMessageDialog(null, "Silakan pilih kategori yang valid!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // validasi keunikan ID (tidak boleh duplikat)
        if (isIdExist(id, -1)) {
            JOptionPane.showMessageDialog(null, "ID '" + id + "' sudah terdaftar! Gunakan ID yang lain.", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int tahunLahir = Integer.parseInt(tahunLahirStr);

            // tambahkan data ke dalam list
            listPerson.add(new Person(id, nama, tahunLahir, kategori, shiftKerja));

            // update tabel
            peopleTable.setModel(setTable());

            // bersihkan form
            clearForm();

            // feedback
            System.out.println("Insert berhasil");
            JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Tahun lahir harus berupa angka!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // updateData
    public void updateData() {
        // ambil data dari form
        String id = idField.getText().trim();
        String nama = namaField.getText().trim();
        String tahunLahirStr = tahunLahirField.getText().trim();
        String kategori = kategoriComboBox.getSelectedItem().toString();
        String shiftKerja = getSelectedShift();

        // validasi field tidak boleh kosong
        if (id.isEmpty() || nama.isEmpty() || tahunLahirStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Semua field harus diisi!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // validasi kategori harus dipilih
        if ("???".equals(kategori)) {
            JOptionPane.showMessageDialog(null, "Silakan pilih kategori yang valid!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // validasi keunikan ID (tidak boleh sama dengan ID data lain)
        if (isIdExist(id, selectedIndex)) {
            JOptionPane.showMessageDialog(null, "ID '" + id + "' sudah digunakan oleh data lain!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int tahunLahir = Integer.parseInt(tahunLahirStr);

            // ubah data orang di list
            listPerson.get(selectedIndex).setId(id);
            listPerson.get(selectedIndex).setNama(nama);
            listPerson.get(selectedIndex).setTahunLahir(tahunLahir);
            listPerson.get(selectedIndex).setKategori(kategori);
            listPerson.get(selectedIndex).setShiftKerja(shiftKerja);

            // update tabel
            peopleTable.setModel(setTable());

            // bersihkan form
            clearForm();

            // feedback
            System.out.println("Update berhasil");
            JOptionPane.showMessageDialog(null, "Data berhasil diubah");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Tahun lahir harus berupa angka!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // deleteData
    public void deleteData() {
        // hapus data dari list
        listPerson.remove(selectedIndex);

        // update tabel
        peopleTable.setModel(setTable());

        // bersihkan form
        clearForm();

        // feedback
        System.out.println("Delete berhasil");
        JOptionPane.showMessageDialog(null, "Data berhasil dihapus");
    }

    // clearForm
    public void clearForm() {
        // kosongkan semua textfield, combo box, dan reset radio button
        idField.setText("");
        namaField.setText("");
        tahunLahirField.setText("");
        kategoriComboBox.setSelectedIndex(0);
        pagiRadioButton.setSelected(true);

        // ubah button "Update" menjadi "Add"
        addUpdateButton.setText("Add");

        // sembunyikan button delete
        deleteButton.setVisible(false);

        // ubah selectedIndex menjadi -1 (tidak ada baris yang dipilih)
        selectedIndex = -1;
    }

    // mengisi listPerson dengan data awal
    private void populateList() {
        listPerson.add(new Person("MED001", "dr. Hendra Wijaya, Sp.JP", 1982, "Dokter Spesialis", "Pagi"));
        listPerson.add(new Person("MED002", "dr. Siti Aminah, Sp.A", 1986, "Dokter Spesialis", "Siang"));
        listPerson.add(new Person("MED003", "dr. Budi Santoso", 1993, "Dokter Umum", "Malam"));
        listPerson.add(new Person("MED004", "Ns. Dewi Lestari, S.Kep", 1996, "Perawat", "Pagi"));
        listPerson.add(new Person("MED005", "apt. Rian Pratama, S.Farm", 1994, "Apoteker", "Siang"));
        listPerson.add(new Person("MED006", "Nadia Putri", 1998, "Staf Administrasi", "Pagi"));
        listPerson.add(new Person("MED007", "Ns. Ahmad Fauzi, S.Kep", 1995, "Perawat", "Malam"));
        listPerson.add(new Person("MED008", "dr. Maya Indah", 1992, "Dokter Umum", "Pagi"));
        listPerson.add(new Person("MED009", "apt. Clarissa Yunita, S.Farm", 1997, "Apoteker", "Malam"));
        listPerson.add(new Person("MED010", "Rizky Ramadhan", 1999, "Staf Administrasi", "Siang"));
        listPerson.add(new Person("MED011", "dr. Bagas Saputra, Sp.PD", 1980, "Dokter Spesialis", "Pagi"));
        listPerson.add(new Person("MED012", "Ns. Lestari Wulandari, S.Kep", 1997, "Perawat", "Siang"));
        listPerson.add(new Person("MED013", "dr. Fajar Nugroho", 1991, "Dokter Umum", "Siang"));
        listPerson.add(new Person("MED014", "Dina Maharani", 2000, "Staf Administrasi", "Malam"));
        listPerson.add(new Person("MED015", "Ns. Ilham Kurniawan, S.Kep", 1994, "Perawat", "Pagi"));
    }
}

