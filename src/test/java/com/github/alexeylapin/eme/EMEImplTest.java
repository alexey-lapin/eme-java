package com.github.alexeylapin.eme;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

public class EMEImplTest {

    // EME-32 encryption test vector from http://grouper.ieee.org/groups/1619/email/pdf00020.pdf
    byte[] buf9F2E = toBytes(new int[]{
            0x9F, 0x2E, 0x6C, 0x3D, 0xAE, 0xCA, 0xE7, 0x9E, 0x88, 0x39, 0xB0, 0x58, 0x8F, 0xF3, 0x78, 0xCD,
            0x06, 0x68, 0x97, 0x0B, 0x95, 0x69, 0x1C, 0xB0, 0x01, 0x82, 0xB9, 0xE3, 0x4C, 0xD6, 0x58, 0xED,
            0x3C, 0x9C, 0x27, 0x68, 0x38, 0xCC, 0x5E, 0x14, 0x11, 0xFC, 0xB8, 0xCF, 0x3D, 0xA1, 0xC0, 0xF3,
            0x08, 0x75, 0x80, 0x4C, 0x9D, 0xF5, 0x11, 0x57, 0xB0, 0x79, 0x11, 0x00, 0xD2, 0x55, 0x13, 0x34,
            0x83, 0x4C, 0xF4, 0x02, 0x4F, 0x6B, 0x71, 0x8F, 0xBC, 0x7D, 0xAB, 0xA0, 0x7D, 0x14, 0xEB, 0x7C,
            0xBC, 0x79, 0xC2, 0x61, 0xB1, 0xEB, 0x03, 0x6D, 0x0C, 0x9F, 0x85, 0xB9, 0x14, 0x38, 0x58, 0x40,
            0x72, 0x72, 0x84, 0x00, 0x5F, 0x06, 0xA9, 0xC1, 0x62, 0x7C, 0x0B, 0x7F, 0xB1, 0x2A, 0x1F, 0x81,
            0xFA, 0x83, 0xC4, 0xB0, 0x35, 0xDB, 0x00, 0x6C, 0xCE, 0x84, 0x6D, 0x07, 0x56, 0xDB, 0x9F, 0xB2,
            0x44, 0x8E, 0xE5, 0x62, 0x8D, 0x23, 0x76, 0xEE, 0x13, 0x95, 0x42, 0x13, 0xDB, 0x3D, 0xCA, 0x72,
            0x5F, 0x2C, 0x67, 0x95, 0x0E, 0xAF, 0x2C, 0xDA, 0xC8, 0xA2, 0x7A, 0x04, 0x33, 0xA1, 0x4C, 0x96,
            0x92, 0x7D, 0x91, 0x45, 0xDD, 0x93, 0xE0, 0xB4, 0x6E, 0x67, 0x0F, 0x6C, 0x4D, 0xB8, 0xAD, 0xD0,
            0x14, 0xB8, 0x88, 0x0E, 0xFB, 0x9A, 0x97, 0xBE, 0xC5, 0xCD, 0x05, 0xBB, 0xA4, 0x3D, 0xCC, 0x35,
            0x05, 0x80, 0x45, 0xAE, 0x81, 0x68, 0xDF, 0x6E, 0x67, 0x77, 0x91, 0x98, 0xFC, 0xC7, 0x28, 0x08,
            0xCE, 0x29, 0xC7, 0xB5, 0xAE, 0xFD, 0xBC, 0x9E, 0x3E, 0xE6, 0x51, 0x17, 0x28, 0x3B, 0xFA, 0x2E,
            0x19, 0x5F, 0x82, 0xCE, 0x19, 0x62, 0xDD, 0x81, 0x12, 0xCB, 0x57, 0xE8, 0x04, 0x0D, 0x77, 0x67,
            0x33, 0xD3, 0xBB, 0x33, 0x1E, 0xA6, 0x30, 0x0F, 0x91, 0xDE, 0xE0, 0xCB, 0xEB, 0x2F, 0xC9, 0xAF,
            0xD3, 0x41, 0xF5, 0x51, 0x5E, 0x22, 0x37, 0x1E, 0x44, 0x2B, 0x86, 0xE7, 0x02, 0x87, 0x54, 0x6A,
            0x16, 0x6E, 0xC2, 0xAE, 0xF8, 0x9F, 0x29, 0x1B, 0xE6, 0x2A, 0xFC, 0x2A, 0x96, 0x89, 0x1E, 0x44,
            0x6E, 0xF6, 0xF1, 0x62, 0x73, 0x55, 0x74, 0xD1, 0x0C, 0xFF, 0x4A, 0x18, 0x3D, 0xE2, 0x76, 0x0B,
            0x5E, 0x14, 0x5D, 0xEA, 0xAD, 0x3E, 0xFD, 0xE1, 0xDA, 0x4B, 0x28, 0x36, 0xC6, 0x65, 0xC5, 0xEC,
            0x4B, 0x54, 0xCB, 0x98, 0x9D, 0x27, 0x73, 0x11, 0xC4, 0x2D, 0xB4, 0x86, 0x2D, 0xB2, 0x92, 0x0C,
            0x39, 0x42, 0x95, 0x8E, 0x54, 0xF6, 0x4E, 0x36, 0x5E, 0x52, 0x19, 0x0E, 0xD8, 0x1A, 0x02, 0xD7,
            0x3B, 0xF7, 0x8A, 0x8A, 0xE5, 0xCC, 0x83, 0xE0, 0x32, 0x03, 0xEF, 0x42, 0x16, 0x14, 0xB7, 0x9A,
            0xE9, 0x84, 0xB6, 0x7E, 0xE9, 0x34, 0x83, 0xD5, 0xEB, 0x1E, 0xA7, 0xB4, 0xFD, 0x95, 0x4C, 0xC3,
            0x50, 0x59, 0xBD, 0x4D, 0x93, 0x2E, 0xF3, 0x42, 0x71, 0x82, 0x50, 0x45, 0xD7, 0x3E, 0xFF, 0xEF,
            0x2E, 0xD3, 0x48, 0x98, 0x71, 0xFD, 0xA2, 0xCC, 0x73, 0x92, 0x4B, 0x4D, 0x45, 0x9D, 0x1C, 0x6E,
            0xE5, 0x25, 0x42, 0x1E, 0x05, 0x50, 0xD3, 0xAB, 0x87, 0x6F, 0x61, 0x53, 0x95, 0xAC, 0x4A, 0x54,
            0xD2, 0x04, 0x78, 0xA4, 0x42, 0xD8, 0x5C, 0x9A, 0x3C, 0x9C, 0x7F, 0xA1, 0x48, 0xF2, 0xB9, 0xDC,
            0xAD, 0xAA, 0x83, 0xCF, 0x40, 0xE9, 0xE4, 0x64, 0xDA, 0x60, 0x36, 0xA5, 0x5C, 0xDB, 0x87, 0x3B,
            0x50, 0xC1, 0x06, 0x0E, 0xCC, 0x27, 0xB4, 0x8D, 0xC0, 0xAF, 0xC7, 0x6E, 0xF7, 0x3F, 0x14, 0x89,
            0x28, 0x1C, 0x08, 0xEF, 0xCE, 0x7F, 0xEC, 0x47, 0xED, 0xD8, 0x23, 0xF2, 0xF5, 0x62, 0xB3, 0x33,
            0xAC, 0x20, 0x9C, 0x2C, 0xD3, 0xCC, 0x57, 0x7C, 0x28, 0xEE, 0xDA, 0xAF, 0xCE, 0xDD, 0x89, 0xA6});

    // EME-32 decryption test vector from http://grouper.ieee.org/groups/1619/email/pdf00020.pdf
    byte[] buf0809 = toBytes(new int[]{
            0x08, 0x09, 0x05, 0xDE, 0xE8, 0xEB, 0xCC, 0x89, 0xF6, 0x8B, 0xD1, 0xAF, 0x63, 0x5D, 0xB3, 0xF5,
            0xB6, 0x0C, 0x2F, 0x13, 0xF7, 0xC7, 0x68, 0xFC, 0xEB, 0x12, 0x20, 0xF6, 0xC2, 0x27, 0xFD, 0x83,
            0x5F, 0x29, 0x3E, 0x85, 0xF1, 0xEA, 0xA8, 0xEE, 0x23, 0x22, 0xF5, 0x42, 0x91, 0xBF, 0x05, 0x1E,
            0x7B, 0x15, 0xAF, 0x84, 0xC7, 0xEA, 0xA4, 0xE8, 0x51, 0x58, 0xAF, 0x7F, 0x4E, 0x6F, 0xF2, 0x4A,
            0x62, 0xBA, 0xCF, 0xF6, 0xDB, 0xF9, 0x1F, 0x43, 0x3F, 0x3B, 0xD5, 0x64, 0xDF, 0xFB, 0xE9, 0xFE,
            0x1B, 0x0E, 0x14, 0xD2, 0x76, 0x87, 0x58, 0x94, 0x98, 0xD5, 0xE8, 0xCA, 0x11, 0xAC, 0xBA, 0x2B,
            0xC6, 0x01, 0x6D, 0x78, 0x23, 0xE3, 0x03, 0x6C, 0x61, 0xCE, 0x97, 0x77, 0xEC, 0x24, 0x45, 0x89,
            0x07, 0x79, 0x02, 0x7F, 0x7D, 0x49, 0x48, 0x93, 0xD9, 0x2F, 0x19, 0xBD, 0xFE, 0x16, 0x0E, 0xF8,
            0x2C, 0x36, 0x06, 0x9C, 0xA8, 0x87, 0xD8, 0x4E, 0xA0, 0x0C, 0xCC, 0x40, 0x13, 0x0C, 0xF7, 0xC4,
            0x11, 0x8C, 0x5D, 0x08, 0x22, 0xA5, 0xE1, 0xF4, 0x93, 0xCD, 0xAE, 0x96, 0xF5, 0x75, 0x20, 0x31,
            0xB4, 0x53, 0xE4, 0xCB, 0x86, 0x08, 0xC8, 0xF2, 0xBA, 0x2C, 0x78, 0xC9, 0x41, 0x12, 0x4C, 0x18,
            0xE3, 0x9F, 0x50, 0xAB, 0x74, 0xB8, 0x31, 0x47, 0xAA, 0x3F, 0xB8, 0x00, 0x53, 0x7E, 0xB9, 0xAC,
            0x55, 0xD7, 0x37, 0x55, 0x2E, 0x05, 0x03, 0x75, 0xF6, 0x07, 0xC5, 0x9B, 0x42, 0x13, 0xD8, 0x7E,
            0x58, 0xE8, 0xDA, 0x6E, 0x23, 0x02, 0x9C, 0x9C, 0xB8, 0x07, 0xAC, 0x63, 0x13, 0x3B, 0x9F, 0xDD,
            0xDA, 0xD8, 0x71, 0x2B, 0xD7, 0x82, 0x11, 0x37, 0xD9, 0xF8, 0xFD, 0xC3, 0xE2, 0x8A, 0xEB, 0x08,
            0xEE, 0x2F, 0xAE, 0x3E, 0xC1, 0xF8, 0x0D, 0x91, 0x26, 0xA3, 0xD2, 0xD0, 0xE4, 0xE4, 0xF1, 0xC6,
            0x42, 0x4C, 0xE6, 0xB5, 0xE9, 0x73, 0xE5, 0x27, 0x03, 0xAF, 0xB3, 0x1C, 0xEE, 0x79, 0x90, 0xDA,
            0x82, 0xB3, 0x16, 0x18, 0x9A, 0xD1, 0x6F, 0xE0, 0x59, 0x92, 0x1C, 0x60, 0xA9, 0x5A, 0x12, 0x08,
            0x71, 0x06, 0x5B, 0x9E, 0xD6, 0x49, 0xD2, 0x11, 0x7D, 0xFB, 0x0C, 0xE5, 0xB5, 0x35, 0x95, 0x11,
            0x9F, 0x21, 0x77, 0xBE, 0xA4, 0x62, 0xF7, 0x66, 0x60, 0xC6, 0xA0, 0x7C, 0x81, 0x0D, 0x21, 0xE1,
            0x85, 0xE2, 0xDA, 0xE5, 0x59, 0xC2, 0x7F, 0x14, 0x09, 0x3F, 0x21, 0xA9, 0x6D, 0x4E, 0x2A, 0x81,
            0x41, 0xD7, 0x6A, 0x3F, 0x96, 0x4A, 0xA7, 0x0B, 0xF7, 0xE9, 0x29, 0xE7, 0x32, 0x24, 0xBD, 0x9F,
            0x17, 0x19, 0xFD, 0xFF, 0x96, 0xBF, 0x4C, 0xA5, 0xDB, 0x51, 0x66, 0x27, 0x22, 0x57, 0x60, 0xF3,
            0xD2, 0xD8, 0x67, 0x0A, 0x4B, 0x82, 0xE1, 0x6A, 0x8B, 0x43, 0x58, 0xEC, 0xD7, 0x81, 0xB0, 0xEE,
            0xA2, 0x2A, 0x29, 0xD0, 0x76, 0x44, 0x24, 0xE9, 0x1E, 0x3D, 0xC7, 0xA6, 0xA1, 0xCE, 0xDD, 0x14,
            0x8C, 0x4B, 0xBB, 0x1B, 0x52, 0x4B, 0x9C, 0x8D, 0xD3, 0xF3, 0xD1, 0x53, 0x40, 0x77, 0x5F, 0xE9,
            0xC9, 0x8E, 0xEC, 0x22, 0x0B, 0x52, 0x4A, 0x8D, 0x95, 0x95, 0xD2, 0xF4, 0x3C, 0x67, 0x83, 0xE6,
            0x03, 0xA3, 0x5B, 0x8D, 0xF9, 0x6A, 0x16, 0x89, 0x75, 0xAC, 0xF5, 0xAC, 0x4E, 0xA4, 0x7E, 0x02,
            0xB7, 0x3A, 0x8C, 0xE6, 0xAF, 0xF8, 0xE5, 0x2D, 0xAD, 0x76, 0x89, 0x79, 0xBD, 0x73, 0x92, 0xB3,
            0x05, 0x0D, 0xD3, 0xB4, 0xE4, 0x79, 0x0E, 0x25, 0xE9, 0xA3, 0x4E, 0xE6, 0x07, 0xDB, 0x5A, 0x58,
            0x5D, 0x16, 0xCA, 0x6B, 0x16, 0xAA, 0x76, 0x37, 0x2A, 0xB4, 0x9E, 0x31, 0xDF, 0x48, 0x65, 0x07,
            0x3A, 0xF8, 0x04, 0xA5, 0xC9, 0xDA, 0xB3, 0x44, 0x20, 0xF2, 0x60, 0xE4, 0xBD, 0x84, 0x08, 0x29});

    @Test
    void encrypt() throws Exception {
        EME eme = EME.fromKey(new byte[32]);

        byte[] encrypt = eme.encrypt(new byte[16], new byte[512]);

        assertThat(encrypt).containsExactly(buf9F2E);
    }

    @Test
    void encryptDecrypt() throws Exception {
        EME eme = EME.fromKey(new byte[32]);

        byte[] encrypt = eme.encrypt(new byte[16], new byte[512]);
        byte[] decrypt = eme.decrypt(new byte[16], encrypt);

        assertThat(decrypt).containsExactly(new byte[512]);
    }

    @RepeatedTest(10)
    void encryptDecryptRandom10times() throws Exception {
        EME eme = EME.fromKey(new byte[32]);

        byte[] tweak = randomBytes(new byte[16]);
        byte[] input = randomBytes(new byte[512]);

        byte[] encrypted = eme.encrypt(tweak, input);
        byte[] decrypted = eme.decrypt(tweak, encrypted);

        assertThat(decrypted).containsExactly(input);
    }

    @Test
    void encrypt100times() throws Exception {
        byte[] key = toBytes(new int[]{
                0x9F, 0x2E, 0x6C, 0x3D, 0xAE, 0xCA, 0xE7, 0x9E, 0x88, 0x39, 0xB0, 0x58, 0x8F, 0xF3, 0x78, 0xCD,
                0x06, 0x68, 0x97, 0x0B, 0x95, 0x69, 0x1C, 0xB0, 0x01, 0x82, 0xB9, 0xE3, 0x4C, 0xD6, 0x58, 0xED});

        byte[] tweak = toBytes(new int[]{
                0x3C, 0x9C, 0x27, 0x68, 0x38, 0xCC, 0x5E, 0x14, 0x11, 0xFC, 0xB8, 0xCF, 0x3D, 0xA1, 0xC0, 0xF3});

        byte[] expected = toBytes(new int[]{
                0x36, 0x00, 0x8C, 0x95, 0xE7, 0x32, 0xA2, 0x31, 0x94, 0x93, 0x7C, 0xC4, 0xDD, 0xED, 0x30, 0xFF,
                0xEE, 0x0F, 0xF6, 0x00, 0xF3, 0xEE, 0x87, 0x96, 0xA5, 0x8A, 0xF9, 0xBB, 0x12, 0x4A, 0xD0, 0x28,
                0x50, 0xFB, 0x30, 0xFA, 0xC7, 0x83, 0x16, 0xA6, 0x46, 0x93, 0xAC, 0xD3, 0x86, 0x02, 0xE4, 0xC7,
                0x04, 0xA4, 0x15, 0x2F, 0xB2, 0xD4, 0x38, 0x3E, 0xEB, 0x1D, 0x85, 0xB1, 0x0F, 0x9E, 0x39, 0xBE,
                0x8D, 0x61, 0x9F, 0x68, 0x93, 0x03, 0xA5, 0xB9, 0xC3, 0xF7, 0xD8, 0x9B, 0xAA, 0x6F, 0x2E, 0x43,
                0xAF, 0xAA, 0x0B, 0xD2, 0xAC, 0x34, 0x52, 0xDA, 0x6A, 0xA2, 0x0F, 0xFF, 0x33, 0xED, 0xB8, 0xF3,
                0x07, 0x24, 0x7D, 0x05, 0x5E, 0xCB, 0xB6, 0xE4, 0xB5, 0x39, 0xC2, 0xC5, 0x30, 0x88, 0xDD, 0xA4,
                0x99, 0xB5, 0xD9, 0x67, 0xF9, 0x8B, 0xCE, 0xC4, 0xA5, 0x4F, 0x4D, 0x27, 0x26, 0x43, 0xE1, 0x3C,
                0x42, 0x26, 0xF6, 0x9E, 0xE6, 0x27, 0xA0, 0x4F, 0x3A, 0xAE, 0xA0, 0x7E, 0x03, 0x3D, 0x3C, 0x4F,
                0x88, 0xA6, 0x50, 0x9C, 0x72, 0x75, 0x88, 0xB1, 0x52, 0xCA, 0x41, 0x41, 0x5D, 0x69, 0x7F, 0xDF,
                0xDD, 0x44, 0x0B, 0x23, 0x86, 0xBB, 0x9A, 0x57, 0x70, 0xCA, 0x28, 0x1C, 0x22, 0x07, 0xD3, 0xEB,
                0x9B, 0x27, 0xFC, 0x6A, 0x2E, 0x48, 0x2E, 0x79, 0x95, 0x88, 0xC7, 0x7B, 0x6B, 0xA3, 0xA1, 0xA4,
                0x66, 0x0E, 0x77, 0xED, 0x70, 0x8A, 0x65, 0xDF, 0x22, 0x86, 0x37, 0x04, 0xBB, 0xE9, 0x44, 0x29,
                0x21, 0x78, 0x36, 0x28, 0x92, 0x86, 0x48, 0x62, 0xD3, 0xC9, 0xA1, 0x8D, 0xD7, 0x04, 0x20, 0xC8,
                0x87, 0xE9, 0x58, 0xA4, 0x30, 0x6E, 0xC8, 0x4F, 0xE7, 0xF6, 0x6D, 0xDC, 0xDE, 0xBA, 0x5B, 0xEE,
                0xDA, 0xB0, 0x32, 0xFB, 0xE8, 0xD4, 0xDD, 0xC4, 0x5B, 0xD4, 0x84, 0x34, 0x9F, 0xD4, 0xCF, 0xF5,
                0xD7, 0x29, 0x90, 0x5F, 0xB5, 0x60, 0xAC, 0x02, 0xBA, 0x1C, 0x83, 0xD8, 0xC5, 0xB7, 0x1F, 0x70,
                0x72, 0x8F, 0x90, 0xD1, 0xD3, 0x5D, 0xB3, 0x65, 0x1A, 0x30, 0x3F, 0x9D, 0xB9, 0xB5, 0x3F, 0xEB,
                0x99, 0x19, 0x44, 0x05, 0xA0, 0x85, 0xF5, 0x43, 0x4E, 0xD1, 0xBB, 0x4E, 0x07, 0x17, 0x22, 0x37,
                0x61, 0x31, 0x63, 0x38, 0x27, 0xC5, 0x4B, 0x86, 0x15, 0x3C, 0x79, 0x28, 0xE5, 0xD9, 0xE5, 0x83,
                0x58, 0xEF, 0x4A, 0x2E, 0xFE, 0xFE, 0x16, 0x5E, 0x94, 0xFE, 0xC5, 0xC2, 0xF0, 0x69, 0x91, 0xD9,
                0xF6, 0x1E, 0xB4, 0xD0, 0xE6, 0xFA, 0x5A, 0x28, 0xD6, 0xED, 0x62, 0x21, 0x6E, 0x4A, 0xDC, 0x2B,
                0x50, 0x7A, 0xE2, 0x3F, 0x25, 0x61, 0x88, 0xE7, 0x40, 0xD4, 0x25, 0xFD, 0xC8, 0x6E, 0x9B, 0x22,
                0x6C, 0xA8, 0xF0, 0x2F, 0x9D, 0x74, 0x60, 0xEE, 0x10, 0xCE, 0xB0, 0xCE, 0x73, 0x06, 0x90, 0x2B,
                0xB5, 0x39, 0x3E, 0x4C, 0x1F, 0xCF, 0xD9, 0x22, 0x6C, 0x57, 0x2C, 0x16, 0x96, 0xE1, 0x5F, 0xFC,
                0xBB, 0xE8, 0x9A, 0x9E, 0xA3, 0xE0, 0x9C, 0xFA, 0x2A, 0xB4, 0x63, 0xA3, 0x7B, 0xA6, 0xEB, 0xED,
                0xCC, 0x02, 0x59, 0x79, 0xFB, 0xC0, 0xED, 0xA8, 0x88, 0xDB, 0x93, 0xEC, 0xAA, 0xC4, 0x48, 0x69,
                0xA1, 0x76, 0xA9, 0x4E, 0x59, 0x56, 0x4E, 0xAF, 0xC8, 0xE9, 0x78, 0x1D, 0xDB, 0xCE, 0x6B, 0x74,
                0xC9, 0x84, 0xEC, 0x1F, 0x27, 0xF7, 0xB9, 0xC0, 0xE4, 0xAE, 0xB7, 0x14, 0xB1, 0x47, 0xE2, 0x79,
                0x34, 0xBF, 0x09, 0xA1, 0x5F, 0x90, 0x13, 0x29, 0x9A, 0x2D, 0x32, 0x07, 0x2A, 0x7C, 0x11, 0x2D,
                0x06, 0x48, 0x52, 0xE0, 0xC3, 0x34, 0x5D, 0x88, 0x34, 0xF1, 0x6F, 0x1F, 0xB2, 0x80, 0xB9, 0xEA,
                0xF8, 0x8C, 0xAD, 0xD4, 0x0C, 0xA2, 0x9C, 0x42, 0x86, 0x66, 0xCF, 0x53, 0x3F, 0xB0, 0x5C, 0x1E});

        EME eme = EME.fromKey(key);

        byte[] out = buf9F2E;
        for (int i = 0; i < 100; i++) {
            out = eme.encrypt(tweak, out);
        }

        assertThat(out).containsExactly(expected);
    }

    @Test
    void decrypt100times() throws Exception {
        byte[] key = toBytes(new int[]{
                0x08, 0x09, 0x05, 0xDE, 0xE8, 0xEB, 0xCC, 0x89, 0xF6, 0x8B, 0xD1, 0xAF, 0x63, 0x5D, 0xB3, 0xF5,
                0xB6, 0x0C, 0x2F, 0x13, 0xF7, 0xC7, 0x68, 0xFC, 0xEB, 0x12, 0x20, 0xF6, 0xC2, 0x27, 0xFD, 0x83});

        byte[] tweak = toBytes(new int[]{
                0x5F, 0x29, 0x3E, 0x85, 0xF1, 0xEA, 0xA8, 0xEE, 0x23, 0x22, 0xF5, 0x42, 0x91, 0xBF, 0x05, 0x1E});

        byte[] expected = toBytes(new int[]{
                0x78, 0xD8, 0xF9, 0xC2, 0xBA, 0xAE, 0xBC, 0xB9, 0x7C, 0x39, 0x14, 0xFE, 0x4F, 0xD9, 0xB9, 0xED,
                0x1B, 0x0F, 0xD0, 0x8C, 0x64, 0xCE, 0x0F, 0x7F, 0xA4, 0x40, 0xC2, 0xB2, 0x31, 0x7C, 0xAC, 0xC6,
                0x10, 0xE7, 0x5A, 0xE2, 0x26, 0xA6, 0x4C, 0x8D, 0xE4, 0x27, 0x36, 0x86, 0x7D, 0xBC, 0x5F, 0xE2,
                0xAC, 0x66, 0x3B, 0x6D, 0xB5, 0x55, 0xD7, 0x9D, 0xC4, 0x80, 0xB7, 0x07, 0xC1, 0x04, 0x11, 0xB8,
                0x31, 0xAA, 0x3E, 0xAA, 0x5A, 0x30, 0x6F, 0xDF, 0x95, 0xC4, 0xEA, 0x06, 0x84, 0xB7, 0x8B, 0xD6,
                0x24, 0x52, 0x75, 0xB5, 0xBC, 0x24, 0x57, 0x58, 0xB2, 0x38, 0x27, 0x4C, 0x2B, 0x7D, 0x7B, 0x8F,
                0xD1, 0xB9, 0x0E, 0x39, 0x0C, 0xD1, 0x0E, 0xD5, 0x4A, 0xD7, 0xD7, 0x22, 0x1A, 0x1A, 0xAE, 0x56,
                0xF8, 0x15, 0xF7, 0x02, 0x6D, 0x3E, 0xE3, 0xFB, 0x12, 0x32, 0xF8, 0x5E, 0x50, 0x0A, 0xE8, 0x75,
                0x6A, 0x53, 0xE2, 0x40, 0x38, 0xE9, 0xD2, 0x54, 0xB4, 0xF0, 0x94, 0x86, 0xF9, 0x5C, 0xAB, 0x88,
                0x25, 0x02, 0xB7, 0x7C, 0x95, 0x79, 0x55, 0x14, 0x90, 0x92, 0x60, 0x31, 0x4F, 0xEB, 0xDF, 0x2A,
                0xC0, 0xD4, 0xFD, 0x47, 0xF5, 0xD6, 0xFD, 0xA2, 0xBA, 0x66, 0xD1, 0xB1, 0x25, 0xA9, 0x00, 0xD7,
                0x8C, 0xAB, 0x58, 0xBF, 0x8E, 0xB9, 0xF2, 0x41, 0xD0, 0x80, 0x06, 0x1A, 0x2E, 0x46, 0xBE, 0x3C,
                0x21, 0xF7, 0x48, 0x45, 0x94, 0x26, 0xF7, 0x9B, 0x61, 0x9E, 0x8C, 0x81, 0x25, 0xF0, 0x6A, 0x60,
                0x7C, 0x9A, 0x55, 0xE4, 0xFD, 0x12, 0xE8, 0x17, 0xE3, 0x90, 0xFB, 0x5F, 0x8C, 0x5A, 0x05, 0x76,
                0xCF, 0xD2, 0x5F, 0x5E, 0x0A, 0xCB, 0x9D, 0xC0, 0x80, 0xB9, 0xC0, 0x1C, 0x7C, 0x9A, 0x41, 0x27,
                0x15, 0x9B, 0x8A, 0x4C, 0xD0, 0xCF, 0xFA, 0xE0, 0xF2, 0x41, 0xBF, 0xBF, 0x8E, 0x41, 0xF2, 0x4D,
                0x50, 0x68, 0xBD, 0x34, 0x54, 0xA9, 0xBE, 0x8E, 0x4F, 0x99, 0x88, 0x1A, 0x7F, 0x6F, 0xF2, 0x1E,
                0x3A, 0x7A, 0x33, 0x70, 0x0F, 0xC1, 0xF8, 0x2B, 0x64, 0x13, 0xE3, 0xF9, 0x72, 0x21, 0xA6, 0x17,
                0x16, 0x15, 0x54, 0x49, 0xCF, 0xE8, 0x7A, 0x3D, 0x57, 0x49, 0xF3, 0x91, 0x96, 0x11, 0xDE, 0xF9,
                0x5D, 0x58, 0xE4, 0x2B, 0xD6, 0xD8, 0x91, 0x43, 0xE3, 0xA0, 0xCA, 0x58, 0x8A, 0x59, 0xB7, 0x9A,
                0x55, 0x06, 0x32, 0xFE, 0xDD, 0x84, 0x62, 0x9A, 0x70, 0x75, 0xB0, 0x89, 0xF2, 0xB0, 0x80, 0x2B,
                0x69, 0xB8, 0x2E, 0xE0, 0xF6, 0x03, 0xF0, 0x3E, 0x99, 0x26, 0x3F, 0xB6, 0x95, 0x19, 0x91, 0xD8,
                0x80, 0x49, 0x63, 0xED, 0xA1, 0x23, 0x1B, 0x25, 0x0D, 0xF5, 0x5E, 0xF7, 0x9E, 0xEF, 0xDE, 0x3C,
                0x99, 0xB9, 0xCD, 0x91, 0xEA, 0xA7, 0x95, 0x63, 0xA9, 0xCD, 0x16, 0x13, 0x6D, 0xB2, 0x43, 0x6F,
                0x4D, 0x72, 0x1F, 0x91, 0x23, 0x94, 0x8A, 0xFC, 0x0B, 0x63, 0x33, 0xCF, 0x2E, 0xD4, 0xCA, 0xAB,
                0xA3, 0x40, 0x4E, 0xDD, 0x2D, 0xE8, 0xF6, 0x55, 0x66, 0x77, 0xC9, 0xB2, 0x86, 0xA2, 0x06, 0x34,
                0x39, 0x4C, 0xB7, 0xEA, 0x72, 0xDD, 0x7E, 0xE3, 0x65, 0x7D, 0x6E, 0xE1, 0xCF, 0xED, 0x8C, 0x3B,
                0x94, 0xB8, 0xBC, 0xC5, 0x78, 0x47, 0x02, 0x57, 0x7F, 0xE4, 0x00, 0xB3, 0x8A, 0x7B, 0x08, 0x95,
                0x74, 0x73, 0xCB, 0x57, 0xEF, 0xB8, 0x61, 0xF2, 0xEB, 0x9E, 0xEC, 0x5A, 0x12, 0x00, 0xCB, 0xD7,
                0x5B, 0x41, 0x43, 0x3F, 0xF1, 0x75, 0x6C, 0xE7, 0x29, 0x88, 0xCA, 0x9A, 0x69, 0x0F, 0x65, 0x97,
                0xCA, 0x0E, 0x8C, 0x98, 0xA1, 0x5C, 0x8B, 0x54, 0x71, 0xBC, 0x11, 0x67, 0x97, 0x8E, 0xC8, 0x3B,
                0xC5, 0xB5, 0x66, 0x0B, 0x4B, 0xC9, 0x93, 0x8A, 0x41, 0xDB, 0xCF, 0x8F, 0xCE, 0x32, 0x1D, 0x1F});

        EME eme = EME.fromKey(key);

        byte[] out = buf0809;
        for (int i = 0; i < 100; i++) {
            out = eme.decrypt(tweak, out);
        }

        assertThat(out).containsExactly(expected);
    }

    private byte[] randomBytes(byte[] bytes) {
        ThreadLocalRandom.current().nextBytes(bytes);
        return bytes;
    }

    private byte[] toBytes(int[] ints) {
        byte[] bytes = new byte[ints.length];
        for (int i = 0; i < ints.length; i++) {
            bytes[i] = (byte) ints[i];
        }
        return bytes;
    }

}