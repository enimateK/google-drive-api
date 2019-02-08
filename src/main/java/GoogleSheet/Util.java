package GoogleSheet;

class Util {
    static int convertColumn(String col) {
        col = col.toUpperCase();
        int sum = 0;
        for(int i = 0 ; i < col.length() ; i++) {
            sum *= 26;
            sum += (col.charAt(i) - 'A' + 1);
        }
        return sum;
    }

    static String convertColumn(int col) {
        StringBuilder sb = new StringBuilder();
        while (col-- > 0) {
            sb.append((char)('A' + (col % 26)));
            col /= 26;
        }
        return sb.reverse().toString();
    }
}
