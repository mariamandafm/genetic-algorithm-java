# Otimização de Genetic Algorithm
Projeto da disciplina Programação Concorrente, consiste na implementação de um  algoritmo genético e ser otimizdo no decorrer do curso.

Algoritmo genético é uma técnica que imita a seleção natural para encontrar a solução mais otimizada.

## Problema
O problema abordado é o classico caixeiro viajente que busca a melhor rota para percorrer um conjunto de lugares.

## Dados de entrada
A aplicação processa uma entrada de 1GB no formato CSV que contém dados de pedidos a serem entregues.

Desses dados serão extraídas todas as coordenadas de pedidos de uma determinada cidade. O algoritmo genético irá processar essas coordenadas a fim de encontrar a melhor rota.

<small>Exemplo dos dados de entrada:</small>
```csv
Pedro Costa,(95) 91099-5899,São Paulo,93681-945,Jardins,Rua das Flores,556,-23,542789,-46,647452
```

A classe `OrderDataGenerator` cria esses dados mocados para serem utilizados na aplicação.

## Versões
| Versão | Tempo de processamento | Descrição                 |
|--------|------------------------|---------------------------|
| v1     |                        | Implementação sequêncial. |