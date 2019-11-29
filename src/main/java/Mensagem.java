import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mensagem {

    @Expose
    int numero_casas;

    @Expose
    String token;

    @Expose
    String cifrado;

    @Expose
    String decifrado;

    @Expose
    String resumo_criptografico;

    @Expose(serialize = false, deserialize = false)
    String erro;

    @Expose(serialize = false, deserialize = false)
    StringBuilder sucesso;

    //Serialize o objeto para impressão
    String mensagemJson() {
        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        Gson gson =  builder.create();
        return gson.toJson(this);
    }

    //Verifica se existe erro na mensagem
    Boolean mensagemStatus() {
        if (this.getErro() == null || this.getErro().isEmpty())
            //não houve erro
            return false;
        else
            //houve erro
            return true;

    }

    void descriptografiaTexto() {
        String cifrado = this.getCifrado();
        int n = this.getNumero_casas();
        int l;

        String decifrado = "";

        for (int i = 0; i < cifrado.length(); i++) {

            //Verifico se é uma letra do alfabeto em minusculo
            if ((int) cifrado.charAt(i) >= 97 && (int) cifrado.charAt(i) <= 122) {

                //Recupero o número em ASCII da letra
                int nL = (int) (cifrado.charAt(i));

                //Se letra for maior que 115(u) retorna para o começo do alfabeto
                if (nL > 113) {
                    l = (((nL + n) - 122) + 96);

                    //Senão retorna o valor da letra
                } else {
                    l = nL + n;
                }
                decifrado = decifrado + (char) (l);

             //Caso não seja uma letra retorno o mesmo
            } else {
                decifrado = decifrado + cifrado.charAt(i);
            }

        }

        this.setDecifrado(decifrado);
    }

    void criptografiaTexto() {
        String sha1 = "";
        //Criptografar a mensagem por sha1
        sha1 = DigestUtils.sha1Hex(this.getDecifrado());
        this.setResumo_criptografico(sha1);
    }
}
