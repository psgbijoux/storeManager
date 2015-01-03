package com.storemanager.util;

public class BareCodeGenerator {
    public static String generateCode7Digit(int catId, int prodId) {
        String _7dgitCode = make7Code(Integer.toString(catId), Integer.toString(prodId));
        return _7dgitCode;
    }

    public static String generateCode8Digit(int catId, int prodId) {
        String _7digit = make7Code(Integer.toString(catId), Integer.toString(prodId));
        int checkSumDigit = getCheckSumDigit(_7digit);
        return _7digit + Integer.toString(checkSumDigit);
    }

    private static String make7Code(String idCat, String idProd) {
        int count = 7 - idCat.length() - idProd.length();
        StringBuilder sb = new StringBuilder();
        sb.append(idCat);
        for (int i = 0; i < count; i++) {
            sb.append("0");
        }
        sb.append(idProd);
        return sb.toString();
    }

    public static int getCheckSumDigit(String codeString) {
        int[] code = new int[7];
        char[] temp = codeString.toCharArray();
        for (int i = 0; i < 7; i++) {
            code[i] = temp[i];
        }
        int sum1 = code[1] + code[3] + code[5];
        int sum2 = 3 * (code[0] + code[2] + code[4] + code[6]);

        int checksum_value = sum1 + sum2;
        int checksum_digit = 10 - (checksum_value % 10);
        if (checksum_digit == 10) checksum_digit = 0;

        return checksum_digit;
    }
}
