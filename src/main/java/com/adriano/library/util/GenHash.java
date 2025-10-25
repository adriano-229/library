package com.adriano.library.util;

public class GenHash {
    public static void main(String[] args) {
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder enc = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(12);
        System.out.println("HASH=" + enc.encode("password123"));
    }
}
