import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Principal extends JFrame {

    public static void main(String[] args) throws Exception {

        //Recebo o token do usuário por parâmetros de execução do programa.
        if (args.length == 0) {
            JOptionPane.showMessageDialog(null, "Infelizmente você não informou o token!",
                    "Criptografia de Júlio César", JOptionPane.PLAIN_MESSAGE);

            //Caso haja o token executa o programa
        } else {

            Mensagem mensagem = new Mensagem();

            //Recebo o tokem passado por parametro na requisicao do programa
            mensagem.setToken(args[0]);

            //Válidar tamanho do token
            if (tokenValido(mensagem.getToken())) {

                //realizo a requisição
                mensagem = requestGET(mensagem);

                //Retorno da mensagem - (True) - houve erro   (False) - não houve erro
                if (mensagem.mensagemStatus()) {
                    JOptionPane.showMessageDialog(null, mensagem.getErro(), "Criptografia de Júlio César", JOptionPane.PLAIN_MESSAGE);
                } else {

                    //Descriptografar a mensagem
                    mensagem.descriptografiaTexto();

                    //Criptografar a mensagem
                    mensagem.criptografiaTexto();

                    //Gravar msg em arquivo
                    criarArquivoJson(mensagem);

                    mensagem.getSucesso().append("Numero_casas: " + mensagem.getNumero_casas() + "\n");
                    mensagem.getSucesso().append("Token: " + mensagem.getToken() + "\n");
                    mensagem.getSucesso().append("Cifrado: " + mensagem.getCifrado() + "\n");
                    mensagem.getSucesso().append("Decifrado: " + mensagem.getDecifrado() + "\n");
                    mensagem.getSucesso().append("Resumo_criptografico: " + mensagem.getResumo_criptografico() + "\n");

                    System.out.println(mensagem.mensagemJson());
                    // mensagem.getSucesso().append(mensagem.mensagemJson());
                    JOptionPane.showMessageDialog(null, mensagem.getSucesso().toString(), "Criptografia de Júlio César", JOptionPane.PLAIN_MESSAGE);
                }
            }

        }


    }

    public static boolean tokenValido(String token) throws Exception {

        //válido se token possui de 30 a 40 carateres
        if (token.length() < 30 || token.length() > 40) {
            JOptionPane.showMessageDialog(null, "Infelizmente você informou um token inválido!",
                    "Criptografia de Júlio César", JOptionPane.PLAIN_MESSAGE);
            return false;
        }
        return true;
    }

    public static Mensagem requestGET(final Mensagem mensagem) throws IOException, ClientProtocolException {

        HttpResponse response;
        Mensagem messageReturn = mensagem;

        try {
            String uri = "https://api.codenation.dev/v1/challenge/dev-ps/generate-data?token=" + mensagem.getToken();

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(uri);
            final String requisicao = "Executando requisição " + request.getRequestLine() + "\n";

            response = client.execute(request);

            if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() <= 300) {
                Gson gson = new Gson();
                String result = EntityUtils.toString(response.getEntity());
                messageReturn = gson.fromJson(result, Mensagem.class);
                messageReturn.setSucesso(new StringBuilder(requisicao));
            } else {
                messageReturn.setErro("Falha na comunicação, código de erro: " + response.getStatusLine().getStatusCode());
            }

        } catch (Exception e) {
            messageReturn.setErro("Falha na comunicação: " + e.getMessage());
        } finally {
            return messageReturn;
        }
    }

    public static void criarArquivoJson(Mensagem mensagem) throws IOException {
        FileWriter arq = new FileWriter("c:\\answer.json");
        PrintWriter gravarArq = new PrintWriter(arq);
        gravarArq.print(mensagem.mensagemJson());
        arq.close();
        mensagem.getSucesso().append("Mensagem gravada com sucesso. c:\\answer.json");
    }
}
