import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useRegisterMutation } from '../features/auth/authApi';
import Logo from '../components/Logo';

const Register = () => {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [register, { isLoading, error }] = useRegisterMutation();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await register({ username, email, password }).unwrap();
      navigate('/login');
    } catch (err) {
      console.error('Failed to register:', err);
    }
  };

  return (
    <div className="relative min-h-screen flex items-center justify-center bg-background overflow-hidden">
      {/* Dynamic Background Mesh */}
      <div className="absolute inset-0 z-0 opacity-30">
        <div className="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] rounded-full bg-primary/20 blur-[120px]"></div>
        <div className="absolute bottom-[-10%] right-[-10%] w-[40%] h-[40%] rounded-full bg-primary/10 blur-[120px]"></div>
      </div>
      
      <div className="relative z-10 w-full max-w-md p-10 bg-black/60 backdrop-blur-xl rounded-2xl shadow-2xl border border-white/5">
        <div className="flex justify-center mb-10 scale-110">
          <Logo />
        </div>
        <h2 className="text-3xl font-black mb-8 text-white tracking-tight uppercase italic text-center">Create Account</h2>
        
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
