import MiniCssExtractPlugin from 'mini-css-extract-plugin'
import path from 'path'
import fs from 'fs'
import { fileURLToPath } from 'url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))

// Discover all *.js files directly in src/main/js/ as separate entry points.
// Each file becomes its own bundle (bundle-<name>.js / bundle-<name>.css).
const jsDir = path.resolve(__dirname, 'src/main/js')
const entries = Object.fromEntries(
    fs.readdirSync(jsDir)
        .filter(f => f.endsWith('.js'))
        .map(f => [path.basename(f, '.js'), path.join(jsDir, f)])
)

const config = {
    mode: 'development',
    devtool: 'source-map',
    entry: entries,
    plugins: [
        new MiniCssExtractPlugin({
            // Relative to output.path — places CSS one level up in /css/
            filename: '../css/bundle-[name].css'
        })
    ],
    module: {
        rules: [
            {
                test: /\.s?css$/i,
                use: [
                    MiniCssExtractPlugin.loader,
                    'css-loader',
                    'sass-loader'
                ]
            },
            {
                // Font files imported by bootstrap-icons are copied to /fonts/
                test: /\.(woff2?|eot|ttf|otf|svg)$/i,
                type: 'asset/resource',
                generator: {
                    filename: '../fonts/[name][ext]'
                }
            }
        ]
    },
    output: {
        filename: 'bundle-[name].js',
        // Thymeleaf serves static content from src/main/resources/static
        path: path.resolve(__dirname, 'src/main/resources/static/js'),
        clean: true
    }
}

export default config

