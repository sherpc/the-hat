<template id="deckProgress">
    <div class="pure-g">
        <div class="pure-u-1" style="height: 50px;">
            <div class="progress-bar">
              <span class="bar">
                <span class="progress" v-bind:style="{width: percent + '%'}">{{explainedWords}} / {{deckSize}} 🎩</span>
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
    body {
        /*background: #eef1f3;*/
    }

    .progress-bar {
        border-radius: 60px;
        overflow: hidden;
        width: 100%;
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
        text-align: right;
    }

    .progress-bar {
        left: 50%;
        max-width: 50%;
        position: relative;
        top: 50%;
        transform: translate3d(-50%,-50%,0);
    }
</style>