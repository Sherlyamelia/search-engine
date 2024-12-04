from flask import Flask, request, jsonify, render_template, send_from_directory
import numpy as np
import pickle as pkl
import tensorflow as tf
from tensorflow.keras.applications.resnet50 import ResNet50, preprocess_input
from tensorflow.keras.preprocessing import image
from tensorflow.keras.layers import GlobalMaxPool2D
from sklearn.neighbors import NearestNeighbors
import os
from numpy.linalg import norm
from werkzeug.utils import secure_filename

app = Flask(__name__)

# Tentukan lokasi folder upload dan folder gambar rekomendasi
UPLOAD_FOLDER = 'upload'
RECOMMENDATION_FOLDER = 'images' 
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

# Pastikan folder upload dan folder static/images ada
if not os.path.exists(UPLOAD_FOLDER):
    os.makedirs(UPLOAD_FOLDER)

if not os.path.exists(RECOMMENDATION_FOLDER):
    os.makedirs(RECOMMENDATION_FOLDER)

# Load data fitur, nama file, dan labels
Image_features = pkl.load(open('Image_features.pkl', 'rb'))
filenames = pkl.load(open('filenames.pkl', 'rb'))

def extract_features_from_images(img_path, model):
    img = image.load_img(img_path, target_size=(224, 224))
    img_array = image.img_to_array(img)
    img_expand_dim = np.expand_dims(img_array, axis=0)
    img_preprocess = preprocess_input(img_expand_dim)
    result = model.predict(img_preprocess).flatten()
    norm_result = result / norm(result)
    return norm_result

# Load model ResNet50 untuk feature extraction
base_model = ResNet50(weights='imagenet', include_top=False, input_shape=(224, 224, 3))
base_model.trainable = False

# Tambahkan GlobalMaxPool2D
model = tf.keras.models.Sequential([base_model, GlobalMaxPool2D()])

# Initialize Nearest Neighbors model
neighbors = NearestNeighbors(n_neighbors=6, algorithm='brute', metric='euclidean')
neighbors.fit(Image_features)

# Route untuk rekomendasi berdasarkan gambar yang diupload
@app.route('/recommend', methods=['POST'])
def recommend():
    if 'file' not in request.files:
        return jsonify({'error': 'No file uploaded'}), 400
    
    file = request.files['file']

    # Simpan file yang diupload
    upload_path = os.path.join(app.config['UPLOAD_FOLDER'], file.filename)
    file.save(upload_path)

    # Ekstraksi fitur dari gambar yang diupload
    input_img_features = extract_features_from_images(upload_path, model)

    # Cari gambar rekomendasi
    distance, indices = neighbors.kneighbors([input_img_features])
    recommended_images = [filenames[idx] for idx in indices.flatten()]

    recommended_images = [image.replace("\\", "/") for image in recommended_images]
    return jsonify({'recommended_images': recommended_images})

# Route untuk menyajikan gambar statis
@app.route('/images/<filename>')
def get_image(filename):
    image_path = os.path.join(RECOMMENDATION_FOLDER, filename)
    return send_from_directory(RECOMMENDATION_FOLDER, filename)

if __name__ == '__main__':
    app.run(debug=True)
