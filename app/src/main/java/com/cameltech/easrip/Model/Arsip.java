package com.cameltech.easrip.Model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Arsip implements Serializable {

    String email;
    String namaDok;
    String noDok;
    String thnDokumen;
    String tglUpload;
    String key;
    String uriDok;
    String jenis;
    String kategori;
    String mapArsip;
    String namaMapArsip;
    String deskripsi;

    public Arsip(){

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNamaDok() {
        return namaDok;
    }

    public void setNamaDok(String namaDok) {
        this.namaDok = namaDok;
    }

    public String getNoDok() {
        return noDok;
    }

    public void setNoDok(String noDok) {
        this.noDok = noDok;
    }

    public String getThnDokumen() {
        return thnDokumen;
    }

    public void setThnDokumen(String thnDokumen) {
        this.thnDokumen = thnDokumen;
    }

    public String getTglUpload() {
        return tglUpload;
    }

    public void setTglUpload(String tglUpload) {
        this.tglUpload = tglUpload;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUriDok() {
        return uriDok;
    }

    public void setUriDok(String uriDok) {
        this.uriDok = uriDok;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
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

    public Arsip(String email, String namaDok, String noDok, String thnDokumen,
                 String tglUpload, String uriDok, String jenis, String deskripsi, String mapArsip, String namaMapArsip) {
        this.email = email;
        this.namaDok = namaDok;
        this.noDok = noDok;
        this.thnDokumen = thnDokumen;
        this.tglUpload = tglUpload;
        this.uriDok = uriDok;
        this.jenis = jenis;
        this.deskripsi = deskripsi;
        this.mapArsip = mapArsip;
        this.namaMapArsip = namaMapArsip;
    }


}
