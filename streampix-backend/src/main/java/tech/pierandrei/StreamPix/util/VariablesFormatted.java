package tech.pierandrei.StreamPix.util;

import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.Locale;

@Component
public class VariablesFormatted {

    /**
     * Método para retornar uma string através de um double formatado (ex: 0.5 > "0,5")
     * @param value - Double para formatar
     * @return - Retorna o double formatado
     */
    public String formatDouble(Double value){
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
        nf.setMaximumFractionDigits(2); // até 2 casas decimais
        nf.setMinimumFractionDigits(1); // garante pelo menos 1 decimal
        return nf.format(value);
    }
}