import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Evaluator {

    public static int calculate(String expr) {
        // Convertim l'string d'entrada en una llista de tokens
        Token[] tokens = Token.getTokens(expr);
        // Efectua el procediment per convertir la llista de tokens en notació RPN
        Stack<Token> operadors = new Stack<>();
        List<Token> nombres = new ArrayList<>();
        boolean posicioOp = false;
        int posParen = 0;
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].getTtype() == Token.Toktype.NUMBER) {
                nombres.add(tokens[i]);
                posicioOp = afegirOperadorsPerOrdreDeJerarquia(tokens, operadors, nombres, posicioOp, i);
            }else if (tokens[i].getTtype() == Token.Toktype.OP) {
                operadors.add(tokens[i]);
                posicioOp = prioritats(tokens, i);
            } else if (tokens[i].getTtype() == Token.Toktype.PAREN) {
                operadors.add(tokens[i]);
                posParen = afegirValorsDeParen(tokens, operadors, nombres, posParen, i);
            }
        }

        while (operadors.iterator().hasNext()) {
            nombres.add(operadors.pop());
        }
        System.out.println(nombres);
        // Finalment, crida a calcRPN amb la nova llista de tokens i torna el resultat
        System.out.println(calcRPN(nombres.toArray(new Token[0])));
        return calcRPN(nombres.toArray(new Token[0]));
    }

    private static int afegirValorsDeParen(Token[] tokens, Stack<Token> operadors, List<Token> nombres, int posParen, int i) {
        boolean opDevantParen;
        posParen = aconseguirPosParen(tokens, posParen, i);
        opDevantParen = (i != 0 && tokens[posParen].getTtype() == Token.Toktype.OP);
        colocarPrioritatParen(tokens, operadors, nombres, opDevantParen, i);
        return posParen;
    }

    private static boolean afegirOperadorsPerOrdreDeJerarquia(Token[] tokens, Stack<Token> operadors, 
                                                              List<Token> nombres, boolean posicioOp, int i) {
        if (posicioOp) {
            while (teJerarquiaMajor(tokens, operadors, i)) {
                nombres.add(operadors.pop());
            }
            posicioOp = false;
        }
        return posicioOp;
    }

    private static int aconseguirPosParen(Token[] tokens, int posParen, int i) {
        if (i != 0 && tokens[i].getTk() == '(' && tokens[i + 1].getTk() != '(') posParen = i - 1;
        return posParen;
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
                nombres.add(operadors.pop());
            }
        }
    }

    private static boolean teJerarquiaMajor(Token[] tokens, Stack<Token> operadors, int i) {
        return operadors.iterator().hasNext() && tokens.length - 1 > i + 1 &&
                jerarquia(operadors.peek().getTk()) >= jerarquia(tokens[i + 1].getTk())
                && operadors.peek().getTtype() != Token.Toktype.PAREN;
    }

    private static boolean prioritats(Token[] tokens, int i) {
        if (tokens[i].getTk() == '^'){
            return true;
        }else if (teMajorJerarquiaQueElAnterior(tokens, i, 2,'/')) {
            return true;
        } else if (teMajorJerarquiaQueElAnterior(tokens, i, 2,'*')) {
            return true;
        } else if (teMajorJerarquiaQueElAnterior(tokens, i, 1,'-')) {
            return true;
        } else return teMajorJerarquiaQueElAnterior(tokens, i, 1, '+');
    }

    private static boolean teMajorJerarquiaQueElAnterior(Token[] tokens, int i, int j, char character) {
        return tokens[i].getTk() == character && tokens.length - 1 > i + 2
                && j >= jerarquia(tokens[i + 2].getTk());
    }

    private static int jerarquia(char car) {
        return switch (car) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            case '^' -> 3;
            default -> -1;
        };
    }

    public static int calcRPN(Token[] list) {
        Stack<Integer> pilaDeNombres = new Stack<>();

        // Calcula el valor resultant d'avaluar la llista de tokens
        for (Token token : list) {
            if (token.getTtype() == Token.Toktype.NUMBER) {
                pilaDeNombres.add(token.getValue());
            } else {
                if (token.getTk() == '-' && pilaDeNombres.size() == 1){
                    int num1 = -1;
                    int num2 = pilaDeNombres.pop();
                    pilaDeNombres.add(operacio('*', num2, num1));
                }else {
                    int num1 = pilaDeNombres.pop();
                    int num2 = pilaDeNombres.pop();
                    pilaDeNombres.add(operacio(token.getTk(), num2, num1));
                }
            }

            if (list.length == 1) {
                break;
            }
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
