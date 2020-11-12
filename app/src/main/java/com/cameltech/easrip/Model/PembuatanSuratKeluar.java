package com.cameltech.easrip.Model;

import java.io.Serializable;

public class PembuatanSuratKeluar implements Serializable {

    String no;
    String email;
    String instansi;
    String alamatInstansi;
    String lampiran;
    String perihal;
    String tanggal;
    String statusPersetujuan;
    String deskripsi;
    String keterangan;
    String key;
    String uri;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public PembuatanSuratKeluar() {

    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
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

    public String getStatusPersetujuan() {
        return statusPersetujuan;
    }

    public void setStatusPersetujuan(String statusPeretujuan) {
        this.statusPersetujuan = statusPeretujuan;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PembuatanSuratKeluar(String email, String no, String instansi, String alamatInstansi, String lampiran, String perihal, String tanggal,
                                String valid, String deskripsi, String uri, String ktrngan) {
        this.email = email;
        this.no = no;
        this.instansi = instansi;
        this.alamatInstansi = alamatInstansi;
        this.lampiran = lampiran;
        this.perihal = perihal;
        this.tanggal = tanggal;
        statusPersetujuan = valid;
        this.deskripsi = deskripsi;
        this.uri = uri;
        keterangan = ktrngan;
    }
}
