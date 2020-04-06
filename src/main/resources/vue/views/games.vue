<template id="games">
    <div>
        <div>
            <ul class="user-overview-list">
                <li v-for="game in games">
                    <a :href="`/games/${game.id}`">{{game.settings.title}} ({{game.settings.wordsCount}} words, {{game.settings.personsCount}} persons)</a>
                </li>
            </ul>
        </div>
        <hr/>
        <div>
            <h3>New game</h3>
            <input v-model="newGame.title" placeholder="New game name">
            <input type="number" v-model.number="newGame.wordsCount" placeholder="Words count">
            <input type="number" v-model.number="newGame.personsCount" placeholder="Persons count">
            <button v-on:click="createGame">Create new game</button>
            <p>debug: {{ newGame }}</p>
        </div>
    </div>
</template>
<script>
    const newGameTemplate = function() {
        return {
            title: "",
            wordsCount: 5,
            personsCount: 6
        };
    };

    Vue.component("games", {
        template: "#games",
        data: () => ({
            games: [],
            newGame: newGameTemplate()
        }),
        created() {
            this.fetchGames();
        },
        methods: {
            createGame() {
                this.newGame.title = this.newGame.title && this.newGame.title.trim();
                var newGame = this.newGame;
                if (!newGame){
                    return
                }

                Vue.http.post("/api/games/", this.newGame).then(() => {
                    console.info("New game created");
                    this.fetchGames();
                    this.newGame = newGameTemplate;
                });
            },
            fetchGames() {
                fetch("/api/games")
                    .then(res => res.json())
                    .then(res => this.games = res)
                    .catch(e => console.error("Error while fetching games: ", e))
            }
        }
    });
</script>
<style>
    ul.user-overview-list {
        padding: 0;
        list-style: none;
    }
    ul.user-overview-list a {
        display: block;
        padding: 16px;
        border-bottom: 1px solid #ddd;
    }
    ul.user-overview-list a:hover {
        background: #00000010;
    }
</style>