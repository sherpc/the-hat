<template id="games">
    <div>
        <div class="pure-g">
            <div class="pure-u-1">
                <form class="pure-form">
                    <fieldset>
                        <input type="text" class="pure-input-1-1 pure-input-lg-1-4" v-model="newGame.title" placeholder="Название игры" required>
                        <label for="wordsCount">
                            <input id="wordsCount" style="width: 50px; display: inline" type="number" v-model.number="newGame.wordsCount" required> слов,
                        </label>
                        <label for="playersCount">
                            <input id="playersCount" style="width: 50px; display: inline" type="number" v-model.number="newGame.playersCount" placeholder="Players count" required> игроков,
                        </label>


                        <button v-on:click.prevent="createGame" type="submit" class="pure-button pure-button-primary">Создать игру</button>
                    </fieldset>
                </form>
            </div>
        </div>
        <div class="pure-g">
            <div class="pure-u-1">
                <table class="pure-table pure-table-horizontal">
                    <thead>
                        <tr>
                            <th>Название</th>
                            <th><i class="fa fa-users"></i></th>
                            <th>Статус</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="game in games">
                            <td><a :href="`/games/${game.id}`">{{game.settings.title}}</a></td>
                            <td>{{game.settings.playersCount}}</td>
                            <td>{{gameStateText(game.state)}}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</template>
<script>
    const newGameTemplate = function() {
        return {
            title: "",
            wordsCount: 5,
            playersCount: 6
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
            },
            gameStateText(state) {
                switch (state) {
                    case 'GatheringParty': return 'Сбор игроков';
                    case 'Playing': return 'Игра началась';
                    case 'Finished': return 'Игра закончена';
                }
            }
        }
    });
</script>
<style>

</style>