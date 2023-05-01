import java.util.ArrayList;
import java.util.List;

public class Token {
    enum Toktype {
        NUMBER, OP, PAREN
    }

    /* Pensa a implementar els "getters" d'aquests atributs */
    private Toktype ttype;

    public Toktype getTtype() {
        return ttype;
    }

    private int value;

    public int getValue() {
        return value;
    }

    private char tk;

    public char getTk() {
        return tk;
    }

    /* Constructor privat. Evita que es puguin construir objectes Token externament*/
    private Token() {
    }

    /* Torna un token de tipus "NUMBER" */
    static Token tokNumber(int value) {

        /*Primer cream el token*/
        Token tokenEnum = new Token();

        /*Assignem que no tindra characters*/
        tokenEnum.tk = ' ';

        /*El valor sera igual al valor que ens passen*/
        tokenEnum.value = value;

        /*I el enum sera tipus number.*/
        tokenEnum.ttype = Toktype.NUMBER;

        /*Finalment retornam el token.*/
        return tokenEnum;
    }

    /* Torna un token de tipus "OP"*/
    static Token tokOp(char c) {

        /*Primer cream el token*/
        Token tokenOp = new Token();

        /*El character sera igual al character que ens passen.*/
        tokenOp.tk = c;

        /*El valor sera igual a 0.*/
        tokenOp.value = 0;

        /*El enum sera tipus operador.*/
        tokenOp.ttype = Toktype.OP;

        /*I finalment retornam el token.*/
        return tokenOp;
    }

    /* Torna un token de tipus "PAREN"*/
    static Token tokParen(char c) {

        /*Primer cream el token*/
        Token tokenParen = new Token();

        /*Posteriorment, el character valdre el mateix que el character que ens passen.*/
        tokenParen.tk = c;

        /*El valor sera igual a 0.*/
        tokenParen.value = 0;

        /*I el enum sera de tipus enum*/
        tokenParen.ttype = Toktype.PAREN;

        /*Finalment retornam el token.*/
        return tokenParen;
    }

    /* Mostra un token (conversió a String)*/
    public String toString() {
        String resultat = "";
        if (getTtype() == Toktype.NUMBER) {
            resultat += " " + getValue();
        } else {
            resultat += " " + getTk();
        }
        return resultat;
    }

    /* Mètode equals. Comprova si dos objectes Token són iguals*/
    public boolean equals(Object o) {
        if (o instanceof Token other) {
            return this.ttype == other.ttype && this.value == other.value && this.tk == other.tk;
        }
        return false;
    }

    /* A partir d'un String, torna una llista de tokens */
    public static Token[] getTokens(String expr) {

        /*Primer creem una llista de tipus token que ens servirà per a guardar tots els tokens.*/
        List<Token> llistaTokens = new ArrayList<>();

        /*Fem una variable que ens servirà per a guardar tot el nombre temporalment.*/
        String nombresEnString = "";

        boolean noEstaEnRPN = true;

        /*Fem un bucle que sira la longitud del string.*/
        for (int i = 0; i < expr.length(); i++) {
            /*Si el character on som és un nombre.*/
            if (expr.charAt(i) - '0' >= 0 && expr.charAt(i) - '0' <= 9 && expr.charAt(i) != ' ') {
                /*El anam sumant al string.*/
                nombresEnString += expr.charAt(i);
                /*Si és un parenthesis.*/
            } else if (expr.charAt(i) == '(' || expr.charAt(i) == ')' && expr.charAt(i) != ' ') {

                if (nombresEnString.length() > 0) {
                    /*Afegim el nombre a la llista.*/
                    llistaTokens.add(tokNumber(Integer.parseInt(nombresEnString)));
                    /*Fem que el string temporal torni a valer zero.*/
                    nombresEnString = "";
                }

                /*I afegim el parenthesis a la llista.*/
                llistaTokens.add(tokParen(expr.charAt(i)));
                /*En el cas de que no es complesqui res del anterior sabrem que es un operador.*/
            } else if (expr.charAt(i) != ' ') {

                /*Si la longitud del string de nombres es major a 0*/
                if (nombresEnString.length() > 0) {
                    /*Afegim el nombre a la llista.*/
                    llistaTokens.add(tokNumber(Integer.parseInt(nombresEnString)));
                    /*Fem que el string temporal torni a valer zero.*/
                    nombresEnString = "";
                }

                /*Si el caràcter és un - i no està en format RPN sabrem pot ser negatiu i farem el següent*/
                if (expr.charAt(i) == '-' && noEstaEnRPN) {

                    /*Si està a la primera posició sabrem que és negatiu i substituirem el caràcter per una !*/
                    if (i == 0) {
                        llistaTokens.add(tokOp('!'));

                        /*Si la funció ens retorna true substituirem el caràcter per una !*/
                    } else if (negatiuEnElCasDeSiEsUnParentesi(llistaTokens)) {
                        llistaTokens.add(tokOp('!'));

                        /*Si la funció ens retorna true substituirem el caràcter per una !*/
                    } else {
                        llistaTokens.add(tokOp(expr.charAt(i)));
                    }
                } else {
                    /*Si no afegim l'operador a la llista.*/
                    llistaTokens.add(tokOp(expr.charAt(i)));
                }

            } else if (expr.charAt(i) == ' ' && nombresEnString.length() > 0) {
                /*Afegim el nombre a la llista.*/
                llistaTokens.add(tokNumber(Integer.parseInt(nombresEnString)));
                /*Fem que el string temporal torni a valer zero.*/
                nombresEnString = "";
            }

            /*Si la longitud de la llista no és igual a 2, la primera posició de la llista no és de tipus nombre i
            la segona posició no és de tipus nombre sabrem que no està en el format RPN*/
            noEstaEnRPN = llistaTokens.size() != 2 || llistaTokens.get(0).getTtype() != Toktype.NUMBER
                    || llistaTokens.get(1).getTtype() != Toktype.NUMBER;
        }

        /*Posteriorment, afegim el nombre si en falta.*/
        if (nombresEnString.length() > 0) {
            /*Afegim el nombre a la llista.*/
            llistaTokens.add(tokNumber(Integer.parseInt(nombresEnString)));
        }


        /*I retornem el resultat.*/
        return llistaTokens.toArray(new Token[0]);
    }

    /* Si l'anterior element de la llista és un operador o una obertura de parèntesis sabrem que és negatiu.*/
    private static boolean negatiuEnElCasDeSiEsUnParentesi(List<Token> llistaTokens) {
        return llistaTokens.get(llistaTokens.size() - 1).getTtype() == Toktype.OP ||
                llistaTokens.get(llistaTokens.size() - 1).getTk() == '(';
    }


}
