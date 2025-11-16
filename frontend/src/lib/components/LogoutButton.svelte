<script lang="ts">
    import { logout } from '$lib/stores/auth';

    let isLoading = false;

    async function handleLogout() {
        isLoading = true;
        try {
            await logout();
        } catch (error) {
            console.error('Logout error:', error);
            isLoading = false;
        }
    }
</script>

<button
    on:click={handleLogout}
    disabled={isLoading}
    class="button logout"
    aria-label="Log out"
>
    {isLoading ? '⏳ Logging out...' : '❄️ Log Out'}
</button>

<style>
    .button {
        padding: 0.75rem 1.5rem;
        font-size: 0.95rem;
        font-weight: 600;
        border-radius: 10px;
        border: none;
        cursor: pointer;
        transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
        text-transform: uppercase;
        letter-spacing: 0.08em;
        outline: none;
        white-space: nowrap;
    }

    .button:focus {
        box-shadow: 0 0 0 4px rgba(196, 30, 58, 0.5), 0 4px 12px rgba(0, 0, 0, 0.2);
    }

    .button.logout {
        background: linear-gradient(135deg, #fc8181 0%, #f56565 100%);
        color: white;
    }

    .button.logout:hover:not(:disabled) {
        background: linear-gradient(135deg, #f687b3 0%, #ed64a6 100%);
        transform: translateY(-3px) scale(1.03);
        box-shadow: 0 6px 16px rgba(196, 30, 58, 0.3);
    }

    .button.logout:active:not(:disabled) {
        transform: translateY(-1px) scale(1.01);
    }

    .button:disabled {
        opacity: 0.7;
        cursor: not-allowed;
        transform: none;
    }

    /* Mobile Responsive */
    @media (max-width: 480px) {
        .button {
            padding: 0.65rem 1.2rem;
            font-size: 0.85rem;
        }
    }
</style>