
# Introdução
Por meio dos conhecimentos adquiridos ao longo das aulas de Algoritmos e Estrutura de Dados III foi possível a implementação de um sistema de 
prontuários baseado em hashing dinâmico. Sendo assim, essa documentação abordará todos os aspectos que foram considerados durante a criação dessa solução, 
além de exemplificar o seu funcionamento por meio de diversos testes. Além disso, foi realizada uma comparação entre os tempos dos processos de inserção e 
busca de prontuários. Essa análise é importante para medir a eficácia e aplicabilidade da solução que foi codificada. 

# Descrição do Problema
O sistema deve criar três arquivos, o arquivo mestre que contém todos os prontuários, que são compostos pelos dados dos pacientes. 
Um arquivo diretório que possui a profundidade global, e um vetor onde cada índice tem o endereço de um bucket. 
E por fim um arquivo índice que é composto por uma série de Buckets que tem seus tamanhos determinados pelo usuário e carregam o cpf e o 
endereço desse cpf no arquivo mestre.Assim que um novo prontuário é criado é
necessário inserir o cpf correspondente no arquivo de índice, se um bucket está cheio é necessário inserir algum registro um split deve ocorrer.
O arquivo do diretório e do índice formam o hashing extensível, que cresce conforme a necessidade e deixa a busca muito mais rápida no arquivo indexado, nesse caso o arquivo mestre de prontuários.

# Modelagem e técnicas utilizadas
A linguagem escolhida para desenvolver a aplicação foi a linguagem Java. Essa escolha foi tomada devido ao extenso número de recursos que ela oferece, principalmente por ser uma linguagem orientada a objetos. Além disso, durante grande parte do curso de Ciência da Computação estamos utilizando Java para fins acadêmicos e isso contribuiu para um melhor entendimento de como a solução desse trabalho seria executada.

Foram criadas cinco classes para conseguir resolver o problema sendo duas classes, ArquivoMestre.java e Diretorio.java as principais classes para inserir nos três arquivos.
A classe ArquivoMestre ficou responsável por inserir no arquivo prontuario.db que é o arquivo mestre com todos os prontuários. Além disso, essa classe chama a Diretorio sempre que insere ou busca algum registro.
A classe Diretorio gerencia tanto o arquivo diretorio.db quanto o indice.db, ela é reponsavel por inserir ou buscar um cpf fornecido pela ArquivoMestre dentro do índice e também realizar a expansão do diretório e do índice quando necessário.
As outras 2 classes são Bucket.java e Registro.java que são estruturas de dados que vão ser inseridas nos arquivos.
