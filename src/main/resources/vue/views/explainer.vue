<template id="explainer">
    <div>
        <div v-if="awaitingPlayerChange">
            Awaiting switching to next player...
        </div>
        <div v-else>
            <p class="is-center">Сейчас ты объясняешь слова игроку <strong>{{activePair.listener.name}}</strong>.</p>
            <form v-if="awaitingPlayer" class="pure-form">
                <button type="submit" class="pure-button button-success center-by-horizontal" v-on:click.prevent="onReady">Готов объяснять!</button>
            </form>
            <div v-else-if="deck.length > 0">
                <p class="word-to-explain is-center">{{randomWord}}</p>
                <form class="pure-form">
                    <button type="submit" class="pure-button button-success center-by-horizontal" v-on:click.prevent="onNextWord">Next!</button>
                </form>
                <div></div>
<!--                <div><button v-on:click="onNextTeam">Change player</button></div>-->
                <timer v-bind:duration="5" v-on:timer-finished="onNextTeam"></timer>
            </div>
            <div v-else>Игра окончена!</div>
        </div>
    </div>
</template>
<script>
    Vue.component("explainer", {
        template: "#explainer",
        data: () => ({
            awaitingPlayer: true,
            deck: [],
            awaitingPlayerChange: false
        }),
        created() {
            this.deck = this.initialDeck.shuffle();
        },
        props: {
            activePair: Object,
            initialDeck: Array
        },
        methods: {
            onReady() {
                this.awaitingPlayer = false;
            },
            onNextWord() {
                this.deck.shift();
                const remainingDeck = Array.from(this.deck);
                this.$emit('remaining-deck', remainingDeck);
                if (this.deck.length === 0) {
                    this.$emit('game-over');
                }
            },
            onNextTeam() {
                this.awaitingPlayerChange = true;
                this.$emit('next-team');
            },
            onTimerFinished() {
                console.log('boom');
            }
        },
        computed: {
            randomWord() {
                return this.deck[0];
            }
        }
    });
    Array.prototype.shuffle = function shuffle() {
        let result = Array.from(this);
        let counter = result.length;
        // While there are elements in the array
        while (counter > 0) {
            // Pick a random index
            let index = Math.floor(Math.random() * counter);

            // Decrease counter by 1
            counter--;

            // And swap the last element with it
            let temp = result[counter];
            result[counter] = result[index];
            result[index] = temp;
        }

        return result;
    }

</script>
<style>
    .word-to-explain {
        font-size: 25px;
    }
</style>