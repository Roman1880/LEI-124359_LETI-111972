# Baba Is You — Projeto POO (CheckPoint 1)

Este projeto foi desenvolvido no âmbito da unidade curricular de **Programação Orientada aos Objetos (POO)**.

## 👥 Autores

- **Roman Havrylenko** — Número: `124359`
- **Carmindo de Severino Delvan** — Número: `111972`

---

## 📌 Descrição do Projeto

Este projeto consiste numa adaptação do jogo **"Baba Is You"**. O objetivo desta primeira fase (CheckPoint 1) é a criação da arquitetura base de classes, a definição da hierarquia dos objetos do jogo e a configuração da interação com o motor gráfico.

---

## 📐 Diagrama de Classes UML (CheckPoint 1)

Abaixo é apresentado o diagrama de classes UML que descreve a estrutura e as relações entre os principais componentes do projeto nesta etapa:

![Diagrama de Classes UML](./UML_Checkpoint1.png)

### Principais Blocos da Arquitetura:
- **`ImageTile`** (`pt.iscte.poo.gui`): Interface para a representação gráfica dos objetos no tabuleiro do jogo[cite: 5].
- **`GameObject`** (`objects`): Classe abstrata base para todas as entidades do jogo (posição, camada de visualização, nome)[cite: 2, 6].
- **Entidades do Jogo** (`objects`): 
  - `Baba` — personagem principal com funcionalidade de movimento (`move`)[cite: 2, 6].
  - `Dirt`, `Wall`, `CrackedWall` — obstáculos e elementos do ambiente[cite: 2, 6].
- **`GameEngine`** (`pt.iscte.poo.game`): *Singleton* que gere o ciclo do jogo e a atualização do estado[cite: 6].
- **`Point2D` & `Direction`** (`pt.iscte.poo.utils`): Classes auxiliares para manipulação de coordenadas e direções[cite: 6].

---

## 🛠 Tecnologias

- **Linguagem de Programação:** Java
- **Paradigma:** Programação Orientada aos Objetos (POO)
- **Ferramenta de Modelação UML:** draw.io / PlantText
