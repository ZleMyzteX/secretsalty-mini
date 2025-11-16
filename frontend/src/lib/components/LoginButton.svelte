<script lang="ts">
    import { login } from '$lib/stores/auth';

    let isLoading = false;

    async function handleLogin() {
        isLoading = true;
        try {
            await login();
        } catch (error) {
            console.error('Login error:', error);
            isLoading = false;
        }
    }
</script>

<button
    on:click={handleLogin}
    disabled={isLoading}
    class="button login"
    aria-label="Log in with Auth0"
>
    {isLoading ? '⏳ Logging in...' : '🎅 Log In with Auth0'}
</button>

<style>
    .button {
        padding: 1rem 2rem;
        font-size: 1.1rem;
        font-weight: 600;
        border-radius: 12px;
        border: none;
        cursor: pointer;
        transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
        box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
        text-transform: uppercase;
        letter-spacing: 0.08em;
        outline: none;
        min-width: 200px;
        white-space: nowrap;
    }

    .button:focus {
        box-shadow: 0 0 0 4px rgba(196, 30, 58, 0.5), 0 8px 20px rgba(0, 0, 0, 0.3);
    }

    .button.login {
        background: linear-gradient(135deg, #c41e3a 0%, #8b0000 100%);
        color: white;
    }

    .button.login:hover:not(:disabled) {
        background: linear-gradient(135deg, #e63946 0%, #a71930 100%);
        transform: translateY(-5px) scale(1.05);
        box-shadow: 0 12px 30px rgba(196, 30, 58, 0.4);
    }

    .button.login:active:not(:disabled) {
        transform: translateY(-2px) scale(1.02);
    }

    .button:disabled {
        opacity: 0.7;
        cursor: not-allowed;
        transform: none;
    }

    /* Mobile Responsive */
    @media (max-width: 480px) {
        .button {
            padding: 0.9rem 1.5rem;
            font-size: 1rem;
            min-width: 100%;
        }
    }
</style>