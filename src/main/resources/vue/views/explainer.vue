<template id="explainer">
    <div>
        <div v-if="awaitingPlayerChange">
            Awaiting switching to next player...
        </div>
        <div v-else>
            <h4>You are explainer</h4>
            Now you explains words to <i>{{activePair.listener.name}}</i>.
            <button v-if="awaitingPlayer" v-on:click="onReady">Ready!</button>
            <div v-else-if="deck.length > 0">
                <div>Next word: <i>{{randomWord}}</i></div>
                <div><button v-on:click="onNextWord">Next!</button></div>
                <div><button v-on:click="onNextPlayer">Change player</button></div>
            </div>
            <div v-else>Game finished!</div>
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
            onNextPlayer() {
                this.awaitingPlayerChange = true;
                this.$emit('next-player');
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