<template id="selectingWords">
    <div class="pure-g">
        <div class="pure-u-1">
            <p>{{player.name}}, добавь в шляпу слова:</p>
            <form class="pure-form pure-form-stacked">
                <div v-for="wordIndex in wordsCount">
                    <input type="text" class="pure-u-1 pure-u-lg-1-4" v-model="wordsEditor[wordIndex-1]" v-bind:placeholder="'слово №' + wordIndex">
                </div>
                <button type="submit" class="pure-button pure-button-primary" :disabled="!allWordsFilledIn || loading" v-on:click.prevent="wordsReady">Готово!</button>
            </form>
        </div>
    </div>
</template>
<script>
    Vue.component("selectingWords", {
        template: "#selectingWords",
        data: function() {
            return {
                wordsEditor: Array.from(this.player.words),
                loading: false
            }
        },
        props: {
            gameId: String,
            player: Object,
            wordsCount: Number
        },
        methods: {
            wordsReady() {
                this.loading = true;
                this.$emit('words-ready', Array.from(this.wordsEditor));
                Vue.http.post(`/api/games/${this.gameId}/${this.player.id}/setWords`, this.wordsEditor).then(response => {
                    return response.json();
                }, err => {
                    console.log(err);
                    this.loading = false;
                }).then(gameContext => {
                    // TODO here we could unblock UI, but new state will arrive in websocket
                });
            }
        },
        computed: {
            allWordsFilledIn() {
                return this.wordsCount == this.wordsEditor.filter(w => w && w.trim()).length;
            }
        }
    });
</script>