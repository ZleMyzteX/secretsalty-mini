<script lang="ts">
    import { onMount } from 'svelte';
    import { initializeAuth } from '$lib/stores/auth';
    import '../app.css';

    onMount(() => {
        initializeAuth();
    });
</script>

<main>
    <slot />
</main>

<style>
    :global(body) {
        margin: 0;
        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', sans-serif;
        background: linear-gradient(135deg, #c41e3a 0%, #165b33 100%);
        min-height: 100vh;
        color: #fff;
        overflow-x: hidden;
    }

    :global(html) {
        scroll-behavior: smooth;
    }

    main {
        min-height: 100vh;
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 1rem;
        box-sizing: border-box;
        position: relative;
    }

    /* Festive snowflake background animation */
    :global(::before) {
        content: '';
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        pointer-events: none;
        z-index: -1;
    }
</style>