import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Evaluator {

    public static int calculate(String expr) {

        /* Bateria de variables utilitzades per a fer el caculate. */
        Token[] tokens = Token.getTokens(expr);
        Stack<Token> operadors = new Stack<>();
        List<Token> nombres = new ArrayList<>();
        boolean posicioOp = false;
        int posParen = 0;

        /* Bucle per a fer la conversio. */
        for (int i = 0; i < tokens.length; i++) {

            /* Si es un nombre. */
            if (tokens[i].getTtype() == Token.Toktype.NUMBER) {
                nombres.add(tokens[i]);
                posicioOp = afegirOperadorsPerOrdreDeJerarquia(tokens, operadors, nombres, posicioOp, i);

                /* Si es un operador. */
            } else if (tokens[i].getTtype() == Token.Toktype.OP) {
                operadors.add(tokens[i]);
                posicioOp = prioritats(tokens, i);

                /* Si es un parentesis. */
            } else if (tokens[i].getTtype() == Token.Toktype.PAREN) {
                operadors.add(tokens[i]);
                posParen = afegirValorsDeParen(tokens, operadors, nombres, posParen, i);
            }
        }
        afegirOperadorsRestants(operadors, nombres);
        return calcRPN(nombres.toArray(new Token[0]));
    }

    private static void afegirOperadorsRestants(Stack<Token> operadors, List<Token> nombres) {
        while (operadors.iterator().hasNext()) {
            nombres.add(operadors.pop());
        }
    }

    private static void colocarPrioritatParen(Token[] tokens, Stack<Token> operadors, List<Token> nombres,
                                              boolean opDevantParen, int i) {
        if (tokens[i].getTk() == ')') {
            operadors.pop();
            while (operadors.peek().getTk() != '(') {
                nombres.add(operadors.pop());
            }
            operadors.pop();
            if (opDevantParen) {
                while (teJerarquiaMajor(tokens, operadors, i)) {
                    nombres.add(operadors.pop());
                }
            }
        }
    }

    private static int afegirValorsDeParen(Token[] tokens, Stack<Token> operadors, List<Token> nombres,
                                           int posParen, int i) {
        posParen = aconseguirPosParen(tokens, posParen, i);
        colocarPrioritatParen(tokens, operadors, nombres, siEsOperadorDevantParen(tokens, posParen, i), i);
        return posParen;
    }

    private static int aconseguirPosParen(Token[] tokens, int posParen, int i) {
        return i != 0 && tokens[i].getTk() == '(' && tokens[i + 1].getTk() != '(' ? i - 1 : posParen;
    }

    private static boolean afegirOperadorsPerOrdreDeJerarquia(Token[] tokens, Stack<Token> operadors,
                                                              List<Token> nombres, boolean posicioOp, int i) {
        if (posicioOp) {
            while (teJerarquiaMajor(tokens, operadors, i)) {
                nombres.add(operadors.pop());
            }
        }
        return false;
    }

    private static boolean teJerarquiaMajor(Token[] tokens, Stack<Token> operadors, int i) {
        return operadors.iterator().hasNext() && tokens.length - 1 > i + 1 &&
                jerarquia(operadors.peek().getTk()) >= jerarquia(tokens[i + 1].getTk())
                && operadors.peek().getTtype() != Token.Toktype.PAREN;
    }

    private static boolean siEsOperadorDevantParen(Token[] tokens, int posParen, int i) {
        return i != 0 && tokens[posParen].getTtype() == Token.Toktype.OP &&
                i + 1 < tokens.length && jerarquia(tokens[posParen].getTk()) >= jerarquia(tokens[i + 1].getTk());
    }

    private static boolean prioritats(Token[] tokens, int i) {
        if (tokens[i].getTk() == '!') {
            return true;
        } else if (teMajorJerarquiaQueElAnterior(tokens, i, 3, '^')) {
            return true;
        } else if (teMajorJerarquiaQueElAnterior(tokens, i, 2, '/')) {
            return true;
        } else if (teMajorJerarquiaQueElAnterior(tokens, i, 2, '*')) {
            return true;
        } else if (teMajorJerarquiaQueElAnterior(tokens, i, 1, '-')) {
            return true;
        } else return teMajorJerarquiaQueElAnterior(tokens, i, 1, '+');
    }


    private static boolean teMajorJerarquiaQueElAnterior(Token[] tokens, int i, int valorJerarquia, char character) {
        return tokens[i].getTk() == character && tokens.length - 1 > i + 2
                && valorJerarquia >= jerarquia(tokens[i + 2].getTk());
    }

    private static int jerarquia(char car) {
        return switch (car) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            case '^' -> 3;
            case '!' -> 4;
            default -> -1;
        };
    }

    public static int calcRPN(Token[] list) {
        Stack<Integer> pilaDeNombres = new Stack<>();
        int num1;
        int num2;
        // Calcula el valor resultant d'avaluar la llista de tokens
        for (Token token : list) {
            if (token.getTtype() == Token.Toktype.NUMBER) {
                pilaDeNombres.add(token.getValue());
            } else {
                if (token.getTk() == '!') {
                    num1 = pilaDeNombres.pop();
                    pilaDeNombres.add(operacio('*', num1, -1));
                } else {
                    num2 = pilaDeNombres.pop();
                    num1 = pilaDeNombres.pop();
                    pilaDeNombres.add(operacio(token.getTk(), num1, num2));
                }
            }
            if (list.length == 1) break;
        }
        return pilaDeNombres.pop();
    }

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
