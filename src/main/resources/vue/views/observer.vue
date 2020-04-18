<template id="observer">
    <p class="is-center">
        Сейчас <i>{{activePair.explainer.name}}</i> объясняет слова <i>{{activePair.listener.name}}</i>.
    </p>
</template>
<script>
    Vue.component("observer", {
        template: "#observer",
        props: {
            activePair: Object
        }
    });
</script>