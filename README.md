# Projeto-BOMSERVIÇO.com---Ferramentas-3
# Luis H Sitolino --------- # Wilker Oliveira



O sistema Web a ser desenvolvido na disciplina consiste em um sistema resumido de classificados/ofertas de serviços. O sistema deverá oferecer o cadastro de categorias de serviços (reparos, transporte, culinária, limpeza, etc..), cadastro de prestadores de serviços e cadastro e gerenciamento de classificados/anuncios. O sistema contará com dois tipos de usuários, sendo o administrador e o prestador de serviço. O administrador terá acesso a todos os cadastros do sistema e poderá incluir, excluir e alterar categorias e também visualizar e excluir anúncios indesejados. O prestador de serviço poderá cadastrar seus próprios dados e inserir, alterar e excluir seus anuncio/classificados. Um usuário qualquer, não registrado, poderá visualizar os anúncios por meio de pesquisas flexíveis e se desejar ele poderá entrar em contato com o prestador por meio de um emissor de mensagens, e assim preencher um formulário contendo o nome, telefone, email e uma mensagem de texto livre do interessado que será encaminhada ao prestador.

     Na especificação do sistema, considere os seguintes módulos:

     a) a página principal com o objetivo de apresentar o sistema e oferecer opções de busca por anúncios e a opção "seja um prestador" com link para cadastro de um novo prestador. Terá acesso livre.
     b) a página do anunciante (prestador de serviços), que necessariamente deverá ter seus dados pessoais, com opção de alteração e uma lista com as mensagens recebidas de interessados. O prestador poderá apagar as mensagens lidas. Terá também um link com opção: "meus anúncios". Somente usuários do nível "prestador" poderá acessar o módulo
     c) a página de cadastro de anúncio/classificado de servico, onde um prestador pode montar seu anúncio, colocando até 3 fotos, além da descrição, horários de atendimento, contato e outras informações que se torne necessária. O prestador também poderá alterar e apagar seu anúncio . Somente usuários do nível "prestador" poderá acessar o módulo
     d) o módulo administrativo: acesso restrito ao administrador, o qual poderá também excluir anuncios de prestadores de serviços e gerenciar (CRUD) as categorias.


Tecnologia a ser utilizada:
lado cliente: HTML5, CSS, JS, AJAX/FETCH API, bibliotecas CSS/JS (Bootstrap, JQuery, ...)
lado servidor: Spring Rest. Spring Data, JWT.
