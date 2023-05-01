import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Evaluator {

    public static int calculate(String expr) {

        /* Bateria de variables utilitzades per a fer el calculate. */
        Token[] tokens = Token.getTokens(expr);
        Stack<Token> operadors = new Stack<>();
        List<Token> nombres = new ArrayList<>();
        boolean posicioOp = false;
        int posParen = 0;

        /* Bucle per a fer la conversió. */
        for (int i = 0; i < tokens.length; i++) {

            /* Si és un nombre. */
            if (tokens[i].getTtype() == Token.Toktype.NUMBER) {

                /*L'afegim a la llista*/
                nombres.add(tokens[i]);

                /*I la posició d'operador valdrà el mateix que la funció */
                posicioOp = afegirOperadorsPerOrdreDeJerarquia(tokens, operadors, nombres, posicioOp, i);

                /* Si és un operador. */
            } else if (tokens[i].getTtype() == Token.Toktype.OP) {

                /*Primer afegim l'operador a la pila d'operadors*/
                operadors.add(tokens[i]);

                /*I la posició d'operador valdrà el mateix que la funció prioritats.*/
                posicioOp = prioritats(tokens, i);

                /* Si és un parèntesi. */
            } else if (tokens[i].getTtype() == Token.Toktype.PAREN) {

                /*Primer afegim el parèntesi a la pila d'operadors*/
                operadors.add(tokens[i]);

                /*I la posició de parèntesi valdrà la funció afegirValorsDeParen*/
                posParen = afegirValorsDeParen(tokens, operadors, nombres, posParen, i);
            }
        }

        /*Finalment afegim els operadors restants*/
        afegirOperadorsRestants(operadors, nombres);

        /*I retornem el resultat de l'operació gracies a la funció calcRPN*/
        return calcRPN(nombres.toArray(new Token[0]));
    }

    /*Funció que amb un bucle while ens afegeix els operadors restants de la pila a la llista de nombres*/
    private static void afegirOperadorsRestants(Stack<Token> operadors, List<Token> nombres) {
        while (operadors.iterator().hasNext()) {
            nombres.add(operadors.pop());
        }
    }

    /*Aquesta funció col·loca per ordre de prioritat els operadors que hi ha dins els parèntesis*/
    private static void colocarPrioritatParen(Token[] tokens, Stack<Token> operadors, List<Token> nombres,
                                              boolean opDevantParen, int i) {

        /*Si la posició a la qual som és igual a un trencament de parèntesi*/
        if (tokens[i].getTk() == ')') {

            /*Trem el primer operador que sera un parèntesi*/
            operadors.pop();

            /*I fem un bucle que afegeix tots els operadors a la llista de nombres*/
            while (operadors.peek().getTk() != '(') {
                nombres.add(operadors.pop());
            }

            /*Finalment, trem l'operador restant que seria l'obertura de parèntesi*/
            operadors.pop();

            /*En el cas que tinguem un operadaor devant el parèntesi*/
            if (opDevantParen) {

                /*Trem els operadors per ordre de jerarquia*/
                while (teJerarquiaMajor(tokens, operadors, i)) {
                    nombres.add(operadors.pop());
                }
            }
        }
    }

    /*Funció que ens dona i col·loca per prioritat el que hi ha dedins els parèntesis*/
    private static int afegirValorsDeParen(Token[] tokens, Stack<Token> operadors, List<Token> nombres,
                                           int posParen, int i) {

        /*Primer aconseguim la posició del parèntesi amb la funció aconseguirPosParen*/
        posParen = aconseguirPosParen(tokens, posParen, i);

        /*Posteriorment, cridem la funció colocarPrioritatParen per a poder col·locar per prioritats el que
        hi ha dedins el parèntesi*/
        colocarPrioritatParen(tokens, operadors, nombres, siEsOperadorDevantParen(tokens, posParen, i), i);

        /*Finalment retornem la posició del parèntesi*/
        return posParen;
    }

    /*Funció que ens retorna la posició del parèntesi*/
    private static int aconseguirPosParen(Token[] tokens, int posParen, int i) {
        return i != 0 && tokens[i].getTk() == '(' && tokens[i + 1].getTk() != '(' ? i - 1 : posParen;
    }

    /*Funció que ens retorna els operadors per ordre*/
    private static boolean afegirOperadorsPerOrdreDeJerarquia(Token[] tokens, Stack<Token> operadors,
                                                              List<Token> nombres, boolean posicioOp, int i) {

        /*Si la posició d'operador és true*/
        if (posicioOp) {

            /*Mirar si te major jerarquia*/
            while (teJerarquiaMajor(tokens, operadors, i)) {

                /*Afegirà els operadors de la pila a la llista*/
                nombres.add(operadors.pop());
            }
        }

        /*Finalment retornarà false*/
        return false;
    }

    /*Funció que mira si l'operador el qual esteem te major jerarquia del que esta darrera ell*/
    private static boolean teJerarquiaMajor(Token[] tokens, Stack<Token> operadors, int i) {
        return operadors.iterator().hasNext() && tokens.length - 1 > i + 1 &&
                jerarquia(operadors.peek().getTk()) >= jerarquia(tokens[i + 1].getTk())
                && operadors.peek().getTtype() != Token.Toktype.PAREN;
    }

    /*Funció que mira si devant del parèntesi té un operador*/
    private static boolean siEsOperadorDevantParen(Token[] tokens, int posParen, int i) {
        return i != 0 && tokens[posParen].getTtype() == Token.Toktype.OP &&
                i + 1 < tokens.length && jerarquia(tokens[posParen].getTk()) >= jerarquia(tokens[i + 1].getTk());
    }

    /*Funció per a mirar les prioritats que té el token que ens passen*/
    private static boolean prioritats(Token[] tokens, int i) {

        /*Si el token en la posició i és el símbol de exclamació !, retorna true.*/
        if (tokens[i].getTk() == '!') {
            return true;

            /*En cas contrari, es comprova si el token en la posició i té major
            jerarquia que el token anterior*/
        } else if (teMajorJerarquiaQueElAnterior(tokens, i, 3, '^')) {
            return true;

            /*Si no es compleixen les condicions anteriors, es comprova si el token en la posició i té major
            jerarquia que el token anterior.*/
        } else if (teMajorJerarquiaQueElAnterior(tokens, i, 2, '/')) {
            return true;

             /*Si no es compleixen les condicions anteriors, es comprova si el token en la posició i té major
            jerarquia que el token anterior.*/
        } else if (teMajorJerarquiaQueElAnterior(tokens, i, 2, '*')) {
            return true;

             /*Si no es compleixen les condicions anteriors, es comprova si el token en la posició i té major
            jerarquia que el token anterior.*/
        } else if (teMajorJerarquiaQueElAnterior(tokens, i, 1, '-')) {
            return true;

            /*Si no es compleixen les condicions anteriors, es comprova si el token en la posició i té major
            jerarquia que el token anterior.*/
        } else return teMajorJerarquiaQueElAnterior(tokens, i, 1, '+');
    }

    /*Funció que mira si te major jerarquia que el anterior*/
    private static boolean teMajorJerarquiaQueElAnterior(Token[] tokens, int i, int valorJerarquia, char character) {
        return tokens[i].getTk() == character && tokens.length - 1 > i + 2
                && valorJerarquia >= jerarquia(tokens[i + 2].getTk());
    }

    /*Funció que ens retorna el nivell de jerarquia de cada operador*/
    private static int jerarquia(char car) {
        return switch (car) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            case '^' -> 3;
            case '!' -> 4;
            default -> -1;
        };
    }

    /*Metode per a calcular una operació expresada en RPN*/
    public static int calcRPN(Token[] list) {
        Stack<Integer> pilaDeNombres = new Stack<>();
        int num1;
        int num2;
        // Calcula el valor resultant d'avaluar la llista de tokens

        /*Bucle que pasa per cada element del array*/
        for (Token token : list) {

            /*Si és un nomber els va inserint dins la llista resultant*/
            if (token.getTtype() == Token.Toktype.NUMBER) {
                pilaDeNombres.add(token.getValue());

                /*Si no fara el següent*/
            } else {

                /*Si és una exclamació simplement multipliquem el nombre per -1*/
                if (token.getTk() == '!') {
                    num1 = pilaDeNombres.pop();
                    pilaDeNombres.add(operacio('*', num1, -1));

                    /*Si no fem l'operació*/
                } else {
                    num2 = pilaDeNombres.pop();
                    num1 = pilaDeNombres.pop();
                    pilaDeNombres.add(operacio(token.getTk(), num1, num2));
                }
            }

            /*Si la llista de longitud 1 atura el bucle*/
            if (list.length == 1) break;
        }

        /*Finalment retornem el resultat*/
        return pilaDeNombres.pop();
    }

    /*Funció que fa l'operació segons el tipus d'operador que li passen*/
    private static Integer operacio(char tk, int num1, int num2) {
        return switch (tk) {
            case '+' -> num1 + num2;
            case '-' -> num1 - num2;
            case '*' -> num1 * num2;
            case '/' -> num1 / num2;
            case '^' -> (int) Math.pow(num1, num2);
            default -> 0;
        };
    }

}
