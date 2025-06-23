/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

/**
 *
 * @author ToiLaDuyGitHub
 */
public class PasswordUtil {

    // Cấu hình mặc định (có thể điều chỉnh)
    private static final int ITERATIONS = 10;
    private static final int MEMORY = 65536; // 64MB
    private static final int PARALLELISM = 1;

    // Tạo hash từ mật khẩu
    public static String hashPassword(String password) {
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        try {
            return argon2.hash(ITERATIONS, MEMORY, PARALLELISM, password.toCharArray());
        } finally {
            argon2.wipeArray(password.toCharArray()); // Xóa mật khẩu tạm thời từ bộ nhớ
        }
    }

    // Kiểm tra mật khẩu với hash
    public static boolean verifyPassword(String hash, String password) {
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        try {
            return argon2.verify(hash, password.toCharArray());
        } finally {
            argon2.wipeArray(password.toCharArray());
        }
    }

    public static void main(String[] args) {

        String password = "hehehe";

        // Hash mật khẩu
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        String hash = argon2.hash(10, 65536, 1, password.toCharArray());
        System.out.println("Hash: " + hash);

        // Xác thực
        boolean isValid = argon2.verify(hash, password.toCharArray());
        System.out.println("Is valid? " + isValid); // true
    }

    public static boolean isValidPassword(String password) {
        return password.matches("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!$@%]).{6,32}$");
    }
}
