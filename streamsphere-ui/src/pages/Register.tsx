import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useRegisterMutation } from '../features/auth/authApi';

const Register = () => {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('USER');
  const [register, { isLoading, error }] = useRegisterMutation();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await register({ username, email, password, role }).unwrap();
      navigate('/login');
    } catch (err) {
      console.error('Failed to register:', err);
    }
  };

  return (
    <div className="relative min-h-screen flex items-center justify-center bg-black bg-opacity-50">
      <div className="absolute inset-0 z-0 bg-[url('https://assets.nflxext.com/ffe/siteui/vlv3/f841d4c2-10f0-4540-a110-387422ba3a93/61da7d05-9888-4d38-8d10-fcddfa09fc9a/US-en-20220502-popsignuptwoweeks-perspective_alpha_website_large.jpg')] bg-cover opacity-50"></div>
      
      <div className="relative z-10 w-full max-w-md p-8 bg-black bg-opacity-75 rounded-md shadow-xl border border-gray-800">
        <h2 className="text-3xl font-bold mb-8 text-white">Create Account</h2>
        
        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <input
              type="text"
              placeholder="Username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full p-4 bg-gray-700 text-white rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
              required
            />
          </div>

          <div>
            <input
              type="email"
              placeholder="Email Address"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full p-4 bg-gray-700 text-white rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
              required
            />
          </div>
          
          <div>
            <input
              type="password"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full p-4 bg-gray-700 text-white rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
              required
            />
          </div>

          <div>
            <select
              value={role}
              onChange={(e) => setRole(e.target.value)}
              className="w-full p-4 bg-gray-700 text-white rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
            >
              <option value="USER">User</option>
              <option value="ADMIN">Admin</option>
            </select>
          </div>

          {error && (
            <p className="text-primary text-sm">Registration failed. Please try again.</p>
          )}

          <button
            type="submit"
            disabled={isLoading}
            className="w-full p-4 bg-primary text-white font-bold rounded-md hover:bg-red-700 transition duration-200 disabled:opacity-50"
          >
            {isLoading ? 'Creating Account...' : 'Sign Up'}
          </button>
        </form>

        <div className="mt-8 text-gray-400">
          Already have an account? <Link to="/login" className="text-white hover:underline">Sign in now</Link>.
        </div>
      </div>
    </div>
  );
};

export default Register;
