import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static org.junit.Assert.assertEquals;

public class Evaluator {

    public static int calculate(String expr) {
        // Convertim l'string d'entrada en una llista de tokens
        Token[] tokens = Token.getTokens(expr);
        // Efectua el procediment per convertir la llista de tokens en notació RPN
        Stack<Token> operadors = new Stack<>();
        List<Token> nombres = new ArrayList<>();
        boolean posicioOp = false;
        int posicioPare = 0;


        for (int i = 0; i < tokens.length; i++) {

            if (tokens[i].getTtype() == Token.Toktype.NUMBER){
                nombres.add(tokens[i]);
                if (posicioOp == true){
                    while (operadors.iterator().hasNext() &&  tokens.length-1 > i+1 &&
                            jerarquia(operadors.peek().getTk()) >= jerarquia(tokens[i+1].getTk())) {
                        nombres.add(operadors.pop());
                    }
                    posicioOp = false;
                }
            }
            if (tokens[i].getTtype() == Token.Toktype.OP){
                operadors.add(tokens[i]);
                posicioOp = prioritats(tokens, posicioOp, i);
            } else if (tokens[i].getTtype() == Token.Toktype.PAREN){
                if(tokens[i].getTk() == '('){
                    posicioPare++;
                }
                if(tokens[i].getTk() == ')'){
                    posicioPare--;
                }
                if (posicioPare == 0) posicioOp = false;
            }
            if (posicioPare > 0){
                posicioOp = true;
            }


        }
        while (operadors.iterator().hasNext()) {
            nombres.add(operadors.pop());
        }

        System.out.println(nombres);
        // Finalment, crida a calcRPN amb la nova llista de tokens i torna el resultat
        Token[] castCalcRPN = new Token[nombres.size()];
        for (int i = 0; i < nombres.size(); i++) {
            castCalcRPN[i] = nombres.get(i);
        }
        int resultat = calcRPN(castCalcRPN);
        return resultat;
    }

    private static boolean prioritats(Token[] tokens, boolean posicioOp, int i) {
        if (tokens[i].getTk() == '/' && tokens.length-1 > i +2 && 2 >= jerarquia(tokens[i +2].getTk())){
            posicioOp = true;
        } else if (tokens[i].getTk() == '*' && tokens.length-1 > i +2 && 2 >= jerarquia(tokens[i +2].getTk())) {
            posicioOp = true;
        } else if (tokens[i].getTk() == '-' && tokens.length-1 > i +2 && 1 == jerarquia(tokens[i +2].getTk())) {
            posicioOp = true;
        } else if (tokens[i].getTk() == '+' && tokens.length-1 > i +2 && 1 == jerarquia(tokens[i +2].getTk())) {
            posicioOp = true;
        }
        return posicioOp;
    }

    private static int jerarquia(char car){
        switch (car){
            case '+':
            case '-':
                return 1 ;
            case '*':
            case '/':
                return 2 ;
            case '^':
                return 3 ;
        }
        return -1;
    }

    public static int calcRPN(Token[] list) {
        Stack<Integer> pilaDeNombres = new Stack<>();

        // Calcula el valor resultant d'avaluar la llista de tokens
        for (int i = 0; i < list.length; i++) {
            if (list[i].getTtype() == Token.Toktype.NUMBER){
                pilaDeNombres.add(list[i].getValue());
            } else {
                int num1 = pilaDeNombres.pop();
                int num2 = pilaDeNombres.pop();
                pilaDeNombres.add(operacio(list[i].getTk(),num2,num1));
            }

            if (list.length == 1){
                break;
            }
        }
        return pilaDeNombres.pop();
    }

    private static Integer operacio(char tk, int num1, int num2) {
        switch (tk){
            case '+':
                return num1 + num2;
            case '-':
                return num1 - num2;
            case '*':
                return num1 * num2;
            case '/':
                return num1 / num2;
            case '^':
                return Math.toIntExact(Math.round(Math.pow(num1, num2)));
        }
       return 0;
    }


}
