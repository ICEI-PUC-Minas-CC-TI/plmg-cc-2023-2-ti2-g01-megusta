package model;

import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Ingrediente {
    private String nome;
    private UUID id;
    private String categoria;
    private JSONObject nutritionalinfo;
    private byte[] imagem; 

    /* Construtor padrão */
    public Ingrediente() {
        setNome("Não informado.");
        setId();
        setCategoria("Não informado.");
        setNutritionalinfo(new JSONObject());
        setImagem(new byte[0]);
    }

    public Ingrediente(String nome, String categoria, String jsonNutritionalInfo, byte[] imagem) {
        setNome(nome);
        setCategoria(categoria);
        setId(); // Chama setId após ter um valor para o id
        setNutritionalinfo(parseJson(jsonNutritionalInfo));
        setImagem(imagem);
    }
    
    public Ingrediente(UUID id, String nome, String categoria, JSONObject nutritionalInfo, byte[] imagem) {
        setNome(nome);
        setCategoria(categoria);
        setId(id); // Já possui um id, então chama setId diretamente
        setNutritionalinfo(nutritionalInfo);
        setImagem(imagem);
    }

    /* SETTERS Métodos que definem o valor dos atributos de um objeto. */

    public void setNome(String nome) {
        this.nome = nome;
    }

    private void setId() {
        this.id = geradorUUID();
    }
    
    private void setId(UUID id) {
        if (id == null) {
            setId(); // Chama setId padrão se id for nulo ou vazio
        } else {
            this.id = id;
        }
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setNutritionalinfo(JSONObject nutritionalinfo) {
        this.nutritionalinfo = nutritionalinfo;
    }
    
    public void setCalorie(double calorie) {
        JSONObject nutritionalinfo = getNutritionalinfo();
        nutritionalinfo.put("calorie", calorie);
        setNutritionalinfo(nutritionalinfo);
    }
    
    public void setCarbohydrate(double carbohydrate) {
        JSONObject nutritionalinfo = getNutritionalinfo();
        nutritionalinfo.put("carbohydrate", carbohydrate);
        setNutritionalinfo(nutritionalinfo);
    }

    public void setProtein(double protein) {
        JSONObject nutritionalinfo = getNutritionalinfo();
        nutritionalinfo.put("protein", protein);
        setNutritionalinfo(nutritionalinfo);
    }
    
    @SuppressWarnings("unchecked")
    public void setFatTransfat(double transfat) {
        JSONObject nutritionalinfo = getNutritionalinfo();
        JSONObject fat = (JSONObject) nutritionalinfo.getOrDefault("fat", new JSONObject());
        fat.put("transfat", transfat);
        nutritionalinfo.put("fat", fat);
        setNutritionalinfo(nutritionalinfo);
    }
    
    @SuppressWarnings("unchecked")
    public void setFatSaturatedfat(double saturatedfat) {
        JSONObject nutritionalinfo = getNutritionalinfo();
        JSONObject fat = (JSONObject) nutritionalinfo.getOrDefault("fat", new JSONObject());
        fat.put("saturatedfat", saturatedfat);
        nutritionalinfo.put("fat", fat);
        setNutritionalinfo(nutritionalinfo);
    }
    
    public void setFiber(double fiber) {
        JSONObject nutritionalinfo = getNutritionalinfo();
        nutritionalinfo.put("fiber", fiber);
        setNutritionalinfo(nutritionalinfo);
    }
    
    public void setSodium(double sodium) {
        JSONObject nutritionalinfo = getNutritionalinfo();
        nutritionalinfo.put("sodium", sodium);
        setNutritionalinfo(nutritionalinfo);
    }

    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }

    /* GETTERS Métodos que retornam o valor dos atributos de um objeto. */

    public String getNome() {
        return nome;
    }

    public UUID getId() {
        return id;
    }

    public String getCategoria() {
        return categoria;
    }
    
    public JSONObject getNutritionalinfo() {
        return nutritionalinfo;
    }
    
    public double getCalorie() {
        return (double) getNutritionalinfo().getOrDefault("calorie", 0.0);
    }
    
    public double getProtein() {
        return (double) getNutritionalinfo().getOrDefault("protein", 0.0);
    }

    public double getCarbohydrate() {
        return (double) getNutritionalinfo().getOrDefault("carbohydrate", 0.0);
    }

    @SuppressWarnings("unchecked")
    public double getFatTransfat() {
        JSONObject nutritionalinfo = getNutritionalinfo();
        JSONObject fat = (JSONObject) nutritionalinfo.getOrDefault("fat", new JSONObject());
        return (double) fat.getOrDefault("transfat", 0.0);
    }

    @SuppressWarnings("unchecked")
    public double getFatSaturatedfat() {
        JSONObject nutritionalinfo = getNutritionalinfo();
        JSONObject fat = (JSONObject) nutritionalinfo.getOrDefault("fat", new JSONObject());
        return (double) fat.getOrDefault("saturatedfat", 0.0);
    }
    
    public double getFiber() {
        return (double) getNutritionalinfo().getOrDefault("fiber", 0.0);
    }
    
    public double getSodium() {
        return (double) getNutritionalinfo().getOrDefault("sodium", 0.0);
    }

    public byte[] getImagem() {
        return imagem;
    }

    private UUID geradorUUID() {
        return UUID.randomUUID();
    }

    private JSONObject parseJson(String json) {
        try {
            return (JSONObject) new JSONParser().parse(json);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}


