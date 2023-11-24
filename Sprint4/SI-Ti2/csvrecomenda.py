from flask import Flask, render_template
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import linear_kernel

app = Flask(__name__)

# Carregar os dados do arquivo CSV
dados_receitas = pd.read_csv('receitas.csv')

# TF-IDF Vectorizer para converter os ingredientes em uma representação vetorial
vetorizador_tfidf = TfidfVectorizer(stop_words='english')
matriz_tfidf = vetorizador_tfidf.fit_transform(dados_receitas['Ingredients'])

# Calcular a similaridade cosseno entre os itens
similaridades_cosseno = linear_kernel(matriz_tfidf, matriz_tfidf)

# Função para obter recomendações
def obter_recomendacoes(titulo, similaridades_cosseno, df):
    indices = df[df['Title'] == titulo].index
    if len(indices) == 0:
        return [{"Title": "Receita não encontrada", "Ingredients": "", "Photo": ""}]
    indice = indices[0]
    pontuacoes_similares = list(enumerate(similaridades_cosseno[indice]))
    pontuacoes_similares = sorted(pontuacoes_similares, key=lambda x: x[1], reverse=True)
    pontuacoes_similares = pontuacoes_similares[1:4]
    indices_receitas = [i[0] for i in pontuacoes_similares]
    recomendacoes = df.iloc[indices_receitas][["Title", "Ingredients", "Image"]].to_dict(orient='records')
    return recomendacoes

# Rota principal
@app.route('/')
def feed():
    receita_titulo = 'Lasanha a bolonhesa'
    recomendacoes_lista = obter_recomendacoes(receita_titulo, similaridades_cosseno, dados_receitas)
    return render_template('feed.html', titulo=receita_titulo, recomendacoes=recomendacoes_lista)

if __name__ == '__main__':
    app.run(debug=True)
