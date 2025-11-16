import { Configuration, type Middleware } from '../generated/runtime';
import { UserControllerApi } from '../generated/apis';
import { AssignmentControllerApi } from '../generated/apis';
import { ExemptionsControllerApi } from '../generated/apis';
import { getToken } from './stores/auth';

/**
 * Middleware that adds JWT token to all API requests
 */
const jwtMiddleware: Middleware = {
    pre: async (context) => {
        const token = await getToken();
        if (token) {
            if (!context.init.headers) {
                context.init.headers = {};
            }
            (context.init.headers as any)['Authorization'] = `Bearer ${token}`;
        }
        return context;
    },
};

/**
 * Create API configuration with JWT token middleware
 */
export const createApiConfiguration = () => {
    return new Configuration({
        basePath: 'http://localhost:8080',
        middleware: [jwtMiddleware],
    });
};

/**
 * Initialize API instances with JWT token middleware
 */
const apiConfig = createApiConfiguration();

export const userApi = new UserControllerApi(apiConfig);
export const assignmentApi = new AssignmentControllerApi(apiConfig);
export const exemptionsApi = new ExemptionsControllerApi(apiConfig);

