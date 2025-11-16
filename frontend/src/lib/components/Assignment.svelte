<script lang="ts">
    import { assignmentApi } from '$lib/api';

    let assignment: { userId: string; username: string } | null = null;
    let isChecking = false;
    let errorMessage: string | null = null;
    let hasChecked = false;

    // Fetch the assignment for the current user
    async function handleCheckAssignment() {
        isChecking = true;
        errorMessage = null;
        hasChecked = true;

        try {
            const result = await assignmentApi.getAssignmentForCurrentUser();

            if (result && result.userId && result.username) {
                assignment = {
                    userId: result.userId,
                    username: result.username
                };
            } else {
                assignment = null;
                errorMessage = 'Assignments not ready yet! Check back soon 🎅';
            }
        } catch (error) {
            console.error('Error checking assignment:', error);
            assignment = null;

            const errorMsg = error instanceof Error ? error.message : 'Failed to check assignment';
            if (errorMsg.includes('404') || errorMsg.includes('not found')) {
                errorMessage = 'Assignments not ready yet! Check back soon 🎅';
            } else {
                errorMessage = errorMsg;
            }
        } finally {
            isChecking = false;
        }
    }
</script>

<div class="assignment-section">
    {#if !hasChecked}
        <!-- Initial State: Button to check assignment -->
        <div class="initial-state">
            <div class="description">
                <p>🎅 Ready to find out who you're buying a gift for?</p>
                <p class="secondary">Click below diggi!</p>
            </div>
            <button
                class="check-button"
                on:click={handleCheckAssignment}
                disabled={isChecking}
            >
                {isChecking ? '⏳ Checking...' : '🎁 Check My Assignment'}
            </button>
        </div>
    {:else}
        <!-- Result State: Show assignment or error -->
        <div class="result-state">
            {#if assignment}
                <!-- Assignment Found -->
                <div class="assignment-found">
                    <div class="assignment-header">
                        <h3 class="assignment-label">🎄 You're buying a gift for:</h3>
                    </div>
                    <div class="assignment-card">
                        <div class="assigned-name">{assignment.username}</div>
                        <div class="assigned-user-id">ID: {assignment.userId}</div>
                    </div>
                    <div class="assignment-footer">
                        <p class="festive-message">✨ Have fun shopping! ✨</p>
                    </div>
                </div>
            {:else if errorMessage}
                <!-- No Assignment Yet -->
                <div class="assignment-not-ready">
                    <div class="message">Assignment not ready yet :(</div>
                    <p class="sub-message">Come back later to see your assignment!</p>
                </div>
            {/if}
        </div>
    {/if}
</div>

<style>
    .assignment-section {
        width: 100%;
    }

    /* Initial State */
    .initial-state {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 1.5rem;
        text-align: center;
        padding: 1rem 0;
    }

    .description {
        display: flex;
        flex-direction: column;
        gap: 0.75rem;
    }

    .description p {
        margin: 0;
        color: #333;
        font-size: 1rem;
    }

    .secondary {
        color: #666;
        font-size: 0.95rem;
    }

    .check-button {
        padding: 1rem 2rem;
        font-size: 1.05rem;
        font-weight: 600;
        border: none;
        border-radius: 12px;
        background: linear-gradient(135deg, #c41e3a 0%, #8b0000 100%);
        color: white;
        cursor: pointer;
        transition: all 0.3s ease;
        box-shadow: 0 4px 15px rgba(196, 30, 58, 0.3);
        text-align: center;
        min-width: 240px;
    }

    .check-button:hover:not(:disabled) {
        transform: translateY(-3px);
        box-shadow: 0 6px 20px rgba(196, 30, 58, 0.4);
    }

    .check-button:active:not(:disabled) {
        transform: translateY(-1px);
    }

    .check-button:disabled {
        opacity: 0.7;
        cursor: not-allowed;
    }

    /* Result State */
    .result-state {
        display: flex;
        flex-direction: column;
        gap: 1.5rem;
        animation: fadeIn 0.4s ease-out;
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

    /* Assignment Found */
    .assignment-found {
        display: flex;
        flex-direction: column;
        gap: 1.5rem;
    }

    .assignment-header {
        text-align: center;
    }

    .assignment-label {
        margin: 0;
        color: #165b33;
        font-size: 1.3rem;
        font-weight: 600;
    }

    .assignment-card {
        background: linear-gradient(135deg, #fff9e6 0%, #fff5cc 100%);
        border: 3px solid #c41e3a;
        border-radius: 12px;
        padding: 2rem;
        text-align: center;
        box-shadow: 0 6px 20px rgba(196, 30, 58, 0.2);
        animation: slideIn 0.5s ease-out;
    }

    @keyframes slideIn {
        from {
            opacity: 0;
            transform: scale(0.95);
        }
        to {
            opacity: 1;
            transform: scale(1);
        }
    }

    .assigned-name {
        font-size: 2rem;
        font-weight: 700;
        color: #c41e3a;
        margin-bottom: 0.5rem;
        word-break: break-word;
    }

    .assigned-user-id {
        font-size: 0.9rem;
        color: #666;
        font-family: 'Courier New', monospace;
    }

    .assignment-footer {
        text-align: center;
    }

    .festive-message {
        margin: 0.5rem 0;
        color: #333;
        font-size: 1rem;
        font-weight: 500;
    }

    /* Assignment Not Ready */
    .assignment-not-ready {
        background: linear-gradient(135deg, #fff0f0 0%, #ffe6e6 100%);
        border: 2px solid #ffc0c0;
        border-radius: 12px;
        padding: 1.5rem;
        text-align: center;
        animation: fadeIn 0.3s ease-out;
    }

    .message {
        color: #c41e3a;
        font-size: 1.1rem;
        font-weight: 600;
        margin: 0;
    }

    .sub-message {
        color: #666;
        font-size: 0.95rem;
        margin: 0.5rem 0 0 0;
    }

    /* Back Button */
    .back-button {
        padding: 0.75rem 1.5rem;
        font-size: 0.95rem;
        font-weight: 600;
        border: none;
        border-radius: 8px;
        background: #ddd;
        color: #333;
        cursor: pointer;
        transition: all 0.2s ease;
        align-self: center;
    }

    .back-button:hover:not(:disabled) {
        background: #ccc;
        transform: translateY(-2px);
    }

    .back-button:disabled {
        opacity: 0.6;
        cursor: not-allowed;
    }

    /* Mobile Responsive */
    @media (max-width: 480px) {
        .check-button {
            min-width: 100%;
            padding: 0.9rem 1.5rem;
            font-size: 0.95rem;
        }

        .assignment-card {
            padding: 1.5rem;
        }

        .assigned-name {
            font-size: 1.5rem;
        }

        .description p {
            font-size: 0.95rem;
        }

        .assignment-label {
            font-size: 1.1rem;
        }
    }
</style>

