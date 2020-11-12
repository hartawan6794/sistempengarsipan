package com.cameltech.easrip.Model;

import java.io.Serializable;

public class SuratMasuk implements Serializable {

    String no;
    String instansi;
    String alamatInstansi;
    String sifat;
    String lampiran;
    String perihal;
    String tanggal;
    String namaSurat;
    String kategori;
    String file;
    String deskripsi;
    String key;
    String mapArsip;
    String namaMapArsip;

    public SuratMasuk(){

    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getInstansi() {
        return instansi;
    }

    public void setInstansi(String instansi) {
        this.instansi = instansi;
    }

    public String getAlamatInstansi() {
        return alamatInstansi;
    }

    public void setAlamatInstansi(String alamatInstansi) {
        this.alamatInstansi = alamatInstansi;
    }

    public String getSifat() {
        return sifat;
    }

    public void setSifat(String sifat) {
        this.sifat = sifat;
    }

    public String getLampiran() {
        return lampiran;
    }

    public void setLampiran(String lampiran) {
        this.lampiran = lampiran;
    }

    public String getPerihal() {
        return perihal;
    }

    public void setPerihal(String perihal) {
        this.perihal = perihal;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getNamaSurat() {
        return namaSurat;
    }

    public void setNamaSurat(String namaSurat) {
        this.namaSurat = namaSurat;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMapArsip() {
        return mapArsip;
    }

    public void setMapArsip(String mapArsip) {
        this.mapArsip = mapArsip;
    }

    public String getNamaMapArsip() {
        return namaMapArsip;
    }

    public void setNamaMapArsip(String namaMapArsip) {
        this.namaMapArsip = namaMapArsip;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public SuratMasuk(String no, String instansi, String alamatInstansi, String sifat, String lampiran
            , String perihal, String tanggal, String namaSurat, String file, String deskripsi
            , String mapArsip, String namaMapArsip,String kategori) {
        this.no = no;
        this.instansi = instansi;
        this.alamatInstansi = alamatInstansi;
        this.sifat = sifat;
        this.lampiran = lampiran;
        this.perihal = perihal;
        this.tanggal = tanggal;
        this.namaSurat = namaSurat;
        this.file = file;
        this.deskripsi = deskripsi;
        this.mapArsip = mapArsip;
        this.namaMapArsip = namaMapArsip;
        this.kategori = kategori;
    }
}
