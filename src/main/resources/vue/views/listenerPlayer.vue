<template id="listenerPlayer">
    <p class="is-center">
        Сейчас <strong>{{activePair.explainer.name}}</strong> объясняет слова тебе.
    </p>
</template>
<script>
    Vue.component("listenerPlayer", {
        template: "#listenerPlayer",
        props: {
            activePair: Object
        }
    });
</script>