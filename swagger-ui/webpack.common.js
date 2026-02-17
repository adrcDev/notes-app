const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
module.exports = {

  entry: {

    index: './src/index.js',

  },

  

  output: {

    filename: '[name].bundle.js',

    path: path.resolve(__dirname, 'dist'),

    clean: true, //cleans the output directory before each build
    
    /*Tell webpack what kind of ES-features may be used in the generated runtime-code. Added so that webpack never uses arrow functions in the
    code inserted by it inside the bundle */
    environment: {
      arrowFunction: false // <-- this line does the trick
    },

    /* The same as output.filename but for Asset Modules. Specifies where to emit the the files processed by asset/resource asset module within the build's output directory (dir in this case)*/
    // assetModuleFilename: 'images/[hash][ext][query]',

    /*Required for react-router to work*/
    publicPath: "/",
    

  },

  /* Specifying loaders and asset modules (inbuilt loaders) to use */
  module: {
    rules: [
      
      /*The below rule uses webpack's asset modules (in built loader).type="asset" implies that webpack will automatically choose between type="asset/resource" and type="asset/inline" by following a default condition: a file with size less than 8kb will be treated as a inline module type and resource asset module type otherwise.*/
      {
        test: /\.(png|gif|jpe?g|jfif|svg|webp)$/,
        type: "asset",
        /*Specifies where the image files will be emitted inside the
        build's output directory (dir in this case) when type="asset/resource" is used as the image file size is > 8kb.*/
        generator: {
          filename: 'images/[hash][ext][query]'
        }
      },

      {
        test: /\.(ttf|otf|woff|woff2|eot)$/,
        type: "asset",
        generator: {
          filename: 'static/fonts/[hash][ext][query]'
        }
      },
      {
        test: /\.(mp3|wav|ogg|flac|aac|m4a|wma|alac|aiff|ape|opus)$/,
        type: "asset",
        generator: {
          filename: 'static/audio files/[hash][ext][query]'
        }
      },
      {
        test: /\.yaml$/,
        type: "asset",
        generator: {
          filename: 'static/yaml/[hash][ext][query]'
        }
      }      
    ]
  },

  //stats: {
    //generate debug logs for babel-loader 
    // loggingDebug: ["babel-loader"] //
  //}, 


 

};
