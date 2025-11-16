<script lang="ts">
    import { isLoggedIn, isLoading } from '$lib/stores/auth';
    import LoginButton from '$lib/components/LoginButton.svelte';
    import LogoutButton from '$lib/components/LogoutButton.svelte';
    import UserProfile from '$lib/components/UserProfile.svelte';
    import Assignment from '$lib/components/Assignment.svelte';
    import Exemptions from '$lib/components/Exemptions.svelte';
</script>

<div class="container">
    {#if $isLoading}
        <div class="loading-screen">
            <div class="loading-spinner">
                <div class="spinner"></div>
            </div>
            <p class="loading-text">Loading Secret Santa... 🎅</p>
        </div>
    {:else if !$isLoggedIn}
        <div class="login-container">
            <div class="festive-header">
                <h1 class="title">🎄 Secret Santa 🎁</h1>
                <p class="subtitle">Your Holiday Gift Exchange Companion</p>
            </div>
            <div class="login-button-wrapper">
                <LoginButton />
            </div>
            <div class="festive-footer">
                <p>✨ Ho Ho Ho! ✨</p>
            </div>
        </div>
    {:else}
        <div class="app-container">
            <div class="app-header">
                <h1 class="app-title">🎄 Secret Santa 🎁</h1>
                <LogoutButton />
            </div>

            <div class="content-wrapper">
                <!-- User Profile Section -->
                <div class="section">
                    <h2 class="section-title">👤 Your Profile</h2>
                    <UserProfile />
                </div>

                <!-- Assignment Section -->
                <div class="section">
                    <h2 class="section-title">🎅 Your Assignment</h2>
                    <Assignment />
                </div>

                <!-- Exemptions Section -->
                <div class="section">
                    <h2 class="section-title">🚫 Your Exemptions</h2>
                    <Exemptions />
                </div>

                <!-- Festive Footer -->
                <div class="festive-message">
                    <p>✨ Spread the holiday cheer! ✨</p>
                    <p>⛄ Merry Christmas! ⛄</p>
                </div>
            </div>
        </div>
    {/if}
</div>

<style>
    .container {
        width: 100%;
        max-width: 500px;
        margin: 0 auto;
    }

    /* Loading Screen */
    .loading-screen {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        gap: 2rem;
        padding: 2rem;
    }

    .loading-spinner {
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .spinner {
        width: 50px;
        height: 50px;
        border: 4px solid rgba(255, 255, 255, 0.3);
        border-top: 4px solid white;
        border-radius: 50%;
        animation: spin 1s linear infinite;
    }

    @keyframes spin {
        to {
            transform: rotate(360deg);
        }
    }

    .loading-text {
        font-size: 1.2rem;
        color: white;
        text-align: center;
        animation: pulse 1.5s ease-in-out infinite;
    }

    @keyframes pulse {
        0%, 100% {
            opacity: 1;
        }
        50% {
            opacity: 0.6;
        }
    }

    /* Login Screen */
    .login-container {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        gap: 2rem;
        padding: 2rem;
        text-align: center;
    }

    .festive-header {
        margin-bottom: 1rem;
    }

    .title {
        font-size: 2.5rem;
        margin: 0;
        color: white;
        text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
        animation: bounce 2s ease-in-out infinite;
    }

    @keyframes bounce {
        0%, 100% {
            transform: translateY(0);
        }
        50% {
            transform: translateY(-10px);
        }
    }

    .subtitle {
        font-size: 1.1rem;
        color: rgba(255, 255, 255, 0.9);
        margin: 0.5rem 0 0 0;
    }

    .login-button-wrapper {
        margin: 1rem 0;
    }

    .festive-footer {
        font-size: 1.3rem;
        color: rgba(255, 255, 255, 0.8);
        animation: twinkle 2s ease-in-out infinite;
    }

    @keyframes twinkle {
        0%, 100% {
            opacity: 1;
        }
        50% {
            opacity: 0.6;
        }
    }

    /* App Container */
    .app-container {
        display: flex;
        flex-direction: column;
        gap: 2rem;
        padding: 1rem;
        animation: slideIn 0.5s ease-out;
    }

    @keyframes slideIn {
        from {
            opacity: 0;
            transform: translateY(20px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    .app-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 1rem;
        margin-bottom: 1rem;
        flex-wrap: wrap;
    }

    .app-title {
        font-size: 2rem;
        margin: 0;
        color: white;
        text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
        flex: 1;
        min-width: 200px;
    }

    .content-wrapper {
        display: flex;
        flex-direction: column;
        gap: 2rem;
    }

    .section {
        background: rgba(255, 255, 255, 0.95);
        border-radius: 16px;
        padding: 1.5rem;
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
        animation: fadeIn 0.6s ease-out;
    }

    @keyframes fadeIn {
        from {
            opacity: 0;
            transform: translateY(10px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    .section-title {
        font-size: 1.4rem;
        margin: 0 0 1rem 0;
        color: #165b33;
        font-weight: 600;
    }

    .festive-message {
        text-align: center;
        color: rgba(255, 255, 255, 0.9);
        font-size: 1.1rem;
        margin-top: 1rem;
    }

    .festive-message p {
        margin: 0.5rem 0;
        animation: twinkle 2s ease-in-out infinite;
    }

    /* Mobile Responsive */
    @media (max-width: 480px) {
        .app-header {
            flex-direction: column;
            align-items: stretch;
        }

        .app-title {
            text-align: center;
            min-width: auto;
        }

        .section {
            padding: 1.2rem;
            border-radius: 12px;
        }

        .title {
            font-size: 2rem;
        }

        .section-title {
            font-size: 1.2rem;
        }
    }
</style>

