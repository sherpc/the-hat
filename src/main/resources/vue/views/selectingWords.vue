<template id="selectingWords">
    <div>
        <p>select words, {{player.name}}</p>
        <div>
            <div v-for="wordIndex in wordsCount">
                <label>word # {{wordIndex}}</label>
                <input v-model="wordsEditor[wordIndex-1]" placeholder="Write word">
            </div>
        </div>
        <button :disabled="!allWordsFilledIn || loading" v-on:click="wordsReady">Ready!</button>
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
            // joinGame() {
            //     const name = this.name && this.name.trim();
            //     if (!this.name) {
            //         return
            //     }
            //     this.$emit('joining');

            // }
            wordsReady() {
                this.loading = true;
                this.$emit('words-ready', Array.from(this.wordsEditor));
                Vue.http.post(`/api/games/${this.gameId}/${this.player.id}/setWords`, this.wordsEditor).then(response => {
                    return response.json();
                }).then(gameContext => {

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