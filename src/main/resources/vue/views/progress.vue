<template id="deckProgress">
    <div class="pure-g">
        <div class="pure-u-lg-1-12 pure-u-1-4 progress-bar-wrapper">
            <div class="progress-bar">{{explainedWords}} / {{deckSize}} 🎩</div>
        </div>
        <div class="pure-u-lg-11-12 pure-u-3-4 progress-bar-wrapper">
            <div class="progress-bar">
                <span class="bar">
                    <span class="progress" v-bind:style="{width: percent + '%'}"></span>
                </span>
            </div>

        </div>
    </div>
</template>
<script>
    Vue.component("deckProgress", {
        template: "#deckProgress",
        props: {
            explainedWords: {
                type: Number,
                required: true
            },
            deckSize: {
                type: Number,
                required: true
            }
        },
        computed: {
            percent() {
                return (this.explainedWords / this.deckSize) * 100;
            }
        }
    });
</script>
<style>
    .progress-bar-wrapper {
        height: 50px;
    }

    .progress-bar {
        border-radius: 60px;
        overflow: hidden;
        width: 100%;
        display: inline-block;
    }

    .progress-bar span {
        display: block;
    }

    .bar {
        background: rgba(0,0,0,0.075);
    }

    .progress {
        background: #75b800;
        color: #fff;
        padding: 0 5px;
        height: 26px;
        text-align: right;
    }

    .progress-bar {
        left: 50%;
        max-width: 95%;
        position: relative;
        top: 50%;
        transform: translate3d(-50%,-50%,0);
    }
</style>