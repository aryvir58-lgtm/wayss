import 'dotenv/config';
import express from 'express';
import cors from 'cors';
import multer from 'multer';
import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import OpenAI from 'openai';
import fs from 'node:fs';
import path from 'node:path';
import crypto from 'node:crypto';
import { fileURLToPath } from 'node:url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const ROOT = path.resolve(__dirname, '..');
const DATA = path.join(__dirname, 'data');
const UPLOADS = path.join(__dirname, 'uploads');
fs.mkdirSync(DATA, { recursive: true });
fs.mkdirSync(UPLOADS, { recursive: true });
const usersFile = path.join(DATA, 'users.json');
if (!fs.existsSync(usersFile)) fs.writeFileSync(usersFile, '[]');

const app = express();
const port = Number(process.env.PORT || 8787);
const jwtSecret = process.env.JWT_SECRET || 'wayss-development-secret-change-me';
const upload = multer({ dest: UPLOADS, limits: { fileSize: Number(process.env.MAX_UPLOAD_MB || 20) * 1024 * 1024 } });
const client = process.env.OPENAI_API_KEY ? new OpenAI({ apiKey: process.env.OPENAI_API_KEY }) : null;

app.use(cors());
app.use(express.json({ limit: '2mb' }));
app.use(express.urlencoded({ extended: true }));
app.use(express.static(path.join(ROOT, 'frontend')));

const readUsers = () => JSON.parse(fs.readFileSync(usersFile, 'utf8'));
const writeUsers = users => fs.writeFileSync(usersFile, JSON.stringify(users, null, 2));
const sign = user => jwt.sign({ sub: user.id, email: user.email, name: user.name }, jwtSecret, { expiresIn: '30d' });
function auth(req, res, next) {
  const token = req.headers.authorization?.replace(/^Bearer\s+/i, '');
  if (!token) return res.status(401).json({ error: 'Authentication required.' });
  try { req.user = jwt.verify(token, jwtSecret); next(); }
  catch { return res.status(401).json({ error: 'Session expired. Please sign in again.' }); }
}
function requireAI(res) {
  if (!client) { res.status(503).json({ error: 'AI is not configured. Add OPENAI_API_KEY in Railway Variables.' }); return false; }
  return true;
}
function cleanName(name, fallback='User') { return String(name || fallback).trim().slice(0, 60); }

app.get('/api/health', (_req, res) => res.json({ ok: true, app: 'Wayss', aiConfigured: Boolean(client) }));

app.post('/api/auth/signup', async (req, res) => {
  const { name, email, password } = req.body || {};
  if (!email || !password || password.length < 8) return res.status(400).json({ error: 'Email and a password of at least 8 characters are required.' });
  const users = readUsers();
  const normalized = String(email).trim().toLowerCase();
  if (users.some(u => u.email === normalized)) return res.status(409).json({ error: 'An account with this email already exists.' });
  const user = { id: crypto.randomUUID(), name: cleanName(name, normalized.split('@')[0]), email: normalized, passwordHash: await bcrypt.hash(password, 12), createdAt: new Date().toISOString() };
  users.push(user); writeUsers(users);
  res.json({ token: sign(user), user: { id: user.id, name: user.name, email: user.email } });
});

app.post('/api/auth/login', async (req, res) => {
  const normalized = String(req.body?.email || '').trim().toLowerCase();
  const password = String(req.body?.password || '');
  const user = readUsers().find(u => u.email === normalized);
  if (!user || !(await bcrypt.compare(password, user.passwordHash))) return res.status(401).json({ error: 'Invalid email or password.' });
  res.json({ token: sign(user), user: { id: user.id, name: user.name, email: user.email } });
});

app.get('/api/me', auth, (req, res) => res.json({ user: { id: req.user.sub, name: req.user.name, email: req.user.email } }));

app.post('/api/chat', auth, async (req, res) => {
  if (!requireAI(res)) return;
  const { messages = [], system } = req.body || {};
  if (!Array.isArray(messages) || !messages.length) return res.status(400).json({ error: 'Messages are required.' });
  const safe = messages.slice(-40).map(m => ({ role: ['user','assistant','system'].includes(m.role) ? m.role : 'user', content: String(m.content || '').slice(0, 30000) }));
  if (system) safe.unshift({ role: 'system', content: String(system).slice(0, 8000) });
  try {
    const completion = await client.chat.completions.create({ model: process.env.OPENAI_MODEL || 'gpt-4.1-mini', messages: safe, temperature: 0.7 });
    res.json({ message: completion.choices?.[0]?.message?.content || 'I could not generate a response.' });
  } catch (e) { res.status(502).json({ error: e?.message || 'AI request failed.' }); }
});

app.post('/api/vision', auth, upload.single('image'), async (req, res) => {
  if (!requireAI(res)) return;
  if (!req.file) return res.status(400).json({ error: 'Image is required.' });
  const prompt = String(req.body.prompt || 'Describe this image in detail.');
  try {
    const base64 = fs.readFileSync(req.file.path).toString('base64');
    const mime = req.file.mimetype || 'image/jpeg';
    const completion = await client.chat.completions.create({
      model: process.env.OPENAI_MODEL || 'gpt-4.1-mini',
      messages: [{ role: 'user', content: [
        { type: 'text', text: prompt },
        { type: 'image_url', image_url: { url: `data:${mime};base64,${base64}` } }
      ] }]
    });
    res.json({ message: completion.choices?.[0]?.message?.content || 'No analysis returned.' });
  } catch (e) { res.status(502).json({ error: e?.message || 'Image analysis failed.' }); }
  finally { fs.unlink(req.file.path, () => {}); }
});

app.post('/api/image/generate', auth, async (req, res) => {
  if (!requireAI(res)) return;
  const prompt = String(req.body?.prompt || '').trim();
  if (!prompt) return res.status(400).json({ error: 'Image prompt is required.' });
  try {
    const result = await client.images.generate({ model: process.env.OPENAI_IMAGE_MODEL || 'gpt-image-1', prompt, size: '1024x1024' });
    const b64 = result.data?.[0]?.b64_json;
    if (!b64) return res.status(502).json({ error: 'The image provider returned no image.' });
    res.json({ image: `data:image/png;base64,${b64}` });
  } catch (e) { res.status(502).json({ error: e?.message || 'Image generation failed.' }); }
});

app.post('/api/image/edit', auth, upload.single('image'), async (req, res) => {
  if (!requireAI(res)) return;
  if (!req.file) return res.status(400).json({ error: 'Image is required.' });
  const prompt = String(req.body?.prompt || '').trim();
  if (!prompt) return res.status(400).json({ error: 'Edit prompt is required.' });
  try {
    const result = await client.images.edit({ model: process.env.OPENAI_IMAGE_MODEL || 'gpt-image-1', image: fs.createReadStream(req.file.path), prompt, size: '1024x1024' });
    const b64 = result.data?.[0]?.b64_json;
    if (!b64) return res.status(502).json({ error: 'The image provider returned no edited image.' });
    res.json({ image: `data:image/png;base64,${b64}` });
  } catch (e) { res.status(502).json({ error: e?.message || 'Image editing failed.' }); }
  finally { fs.unlink(req.file.path, () => {}); }
});

app.post('/api/search', auth, async (req, res) => {
  const query = String(req.body?.query || '').trim();
  if (!query) return res.status(400).json({ error: 'Search query is required.' });
  if (!process.env.TAVILY_API_KEY) return res.status(503).json({ error: 'Web search is not configured. Add TAVILY_API_KEY in Railway Variables.' });
  try {
    const r = await fetch('https://api.tavily.com/search', { method: 'POST', headers: { 'content-type': 'application/json' }, body: JSON.stringify({ api_key: process.env.TAVILY_API_KEY, query, search_depth: 'advanced', max_results: 8, include_answer: true }) });
    const data = await r.json();
    if (!r.ok) throw new Error(data?.detail || 'Search provider failed.');
    res.json({ answer: data.answer || '', results: (data.results || []).map(x => ({ title: x.title, url: x.url, content: x.content })) });
  } catch (e) { res.status(502).json({ error: e?.message || 'Web search failed.' }); }
});

app.post('/api/files/read', auth, upload.single('file'), async (req, res) => {
  if (!req.file) return res.status(400).json({ error: 'File is required.' });
  const allowedText = /\.(txt|md|json|js|ts|tsx|jsx|html|css|py|java|kt|xml|csv|sql|yaml|yml)$/i.test(req.file.originalname);
  try {
    if (!allowedText) return res.json({ name: req.file.originalname, type: req.file.mimetype, note: 'Binary/PDF/DOCX file received. Text extraction is not enabled in this prototype.', text: '' });
    const text = fs.readFileSync(req.file.path, 'utf8').slice(0, 120000);
    res.json({ name: req.file.originalname, type: req.file.mimetype, text });
  } catch { res.status(400).json({ error: 'Could not read the file.' }); }
  finally { fs.unlink(req.file.path, () => {}); }
});

app.get('*', (_req, res) => res.sendFile(path.join(ROOT, 'frontend', 'index.html')));
app.listen(port, () => console.log(`Wayss running on port ${port}`));
